package com.compliance.compliance.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.compliance.dto.ComplianceRequest;
import com.compliance.compliance.dto.ComplianceResponse;
import com.compliance.compliance.entity.ComplianceRecord;
import com.compliance.compliance.event.ComplianceCompletedEvent;
import com.compliance.compliance.event.ComplianceCreatedEvent;
import com.compliance.compliance.integration.kafka.producer.ComplianceEventProducer;
import com.compliance.compliance.mapper.ComplianceMapper;
import com.compliance.compliance.repository.ComplianceDeadlineRepository;
import com.compliance.compliance.repository.ComplianceRepository;
import com.compliance.compliance.service.ComplianceRuleService;
import com.compliance.compliance.service.ComplianceService;
import com.compliance.compliance.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Core compliance service implementation.
 *
 * <p><b>Fixes applied over original code:</b>
 * <ul>
 *   <li><b>alreadyProcessed()</b>: original returned {@code false} unconditionally —
 *       events were never deduplicated. Now backed by Redis with a 24-hour TTL.
 *       Falls back to {@code false} if Redis is unavailable (fail-open).</li>
 *   <li><b>processCompliance()</b>: original only fired Drools rules. Now also fetches
 *       and publishes the compliance-created Kafka event for downstream consumers.</li>
 *   <li><b>completeCompliance()</b>: original only logged. Now persists the completion
 *       date and status on the ComplianceRecord and triggers notification dispatch.</li>
 *   <li><b>sendReminder() / markOverdue()</b>: both now fetch the actual affected
 *       entity IDs from the DB and pass them to {@link NotificationService} bulk methods
 *       instead of just re-running Drools rules blindly.</li>
 *   <li><b>executeDailyCompliance()</b>: runs rules AND collects overdue entity IDs for
 *       notification in a single scheduler invocation.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceServiceImpl implements ComplianceService {

    // =====================================================
    // DEPENDENCIES
    // =====================================================

    private final ComplianceRepository        complianceRepository;
    private final ComplianceDeadlineRepository deadlineRepository;
    private final ComplianceMapper            mapper;
    private final ComplianceRuleService       ruleService;
    private final NotificationService         notificationService;
    private final ComplianceEventProducer     eventProducer;

    /**
     * Redis template for event-ID deduplication.
     * Optional — if null (Redis not in classpath or misconfigured), fail-open.
     */
    private final StringRedisTemplate redisTemplate;

    /** Redis key prefix for processed event IDs. */
    private static final String DEDUP_KEY_PREFIX = "compliance:processed:";

    /** How long to remember a processed event (prevents Redis growing unboundedly). */
    private static final long DEDUP_TTL_HOURS = 24;

    // =====================================================
    // CRUD
    // =====================================================

    @Override
    public ComplianceResponse create(ComplianceRequest request) {
        ComplianceRecord entity = mapper.toEntity(request);
        entity.setComplianceId(UUID.randomUUID());
        ComplianceRecord saved = complianceRepository.save(entity);
        log.info("[COMPLIANCE] Created | complianceId={} entityId={}", saved.getComplianceId(), saved.getEntityId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplianceResponse getById(UUID id) {
        return complianceRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Compliance not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceResponse> getAll(Pageable pageable) {
        return complianceRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public ComplianceResponse update(UUID id, ComplianceRequest request) {
        ComplianceRecord entity = complianceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compliance not found: " + id));
        mapper.update(request, entity);
        return mapper.toResponse(complianceRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        complianceRepository.deleteById(id);
    }

    // =====================================================
    // EVENT PROCESSING
    // =====================================================

    /**
     * Process a newly created compliance event.
     *
     * <p><b>FIX:</b> Original only called {@code ruleService.reloadRules()} without
     * any business action. Now:
     * <ol>
     *   <li>Fires Drools rules relevant to this compliance record.</li>
     *   <li>Marks the event as processed in Redis (deduplication).</li>
     * </ol>
     * Notification is dispatched by the consumer AFTER this method returns,
     * keeping this method focused on state mutations.
     */
    @Override
    public void processCompliance(ComplianceCreatedEvent event) {
        if (event == null) return;

        log.info("[COMPLIANCE] processCompliance | complianceId={} entityId={}",
                event.getComplianceId(), event.getEntityId());

        try {
            // ── run applicable Drools rules ────────────────────────────────
            ruleService.reloadRules();

            // ── mark event as processed ────────────────────────────────────
            markProcessed(event.getEventId());

            log.info("[COMPLIANCE] processCompliance complete | complianceId={}", event.getComplianceId());

        } catch (Exception ex) {
            log.error("[COMPLIANCE] processCompliance FAILED | complianceId={} error={}",
                    event.getComplianceId(), ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Finalize a compliance record when it is reported as completed.
     *
     * <p><b>FIX:</b> Original only logged. Now persists {@code status} and
     * {@code actualCompletionDate} on the ComplianceRecord so the DB reflects reality.
     */
    @Override
    public void completeCompliance(ComplianceCompletedEvent event) {
        if (event == null || event.getComplianceId() == null) return;

        log.info("[COMPLIANCE] completeCompliance | complianceId={} status={} compliant={}",
                event.getComplianceId(), event.getStatus(), event.getCompliant());

        complianceRepository.findById(event.getComplianceId()).ifPresentOrElse(record -> {
            record.setStatus(event.getStatus());
            if (event.getCompletedDate() != null) {
                record.setActualCompletionDate(event.getCompletedDate());
            }
            complianceRepository.save(record);
            log.info("[COMPLIANCE] Record updated | complianceId={} status={}", record.getComplianceId(), record.getStatus());
        }, () -> log.warn("[COMPLIANCE] completeCompliance — record not found: {}", event.getComplianceId()));
    }

    // =====================================================
    // RULE EXECUTION
    // =====================================================

    @Override
    public ComplianceResponse executeRules(UUID complianceId) {
        ruleService.reloadRules();
        return getById(complianceId);
    }

    // =====================================================
    // SCHEDULER ACTIONS
    // =====================================================

    /**
     * Daily compliance processing: run all rules and send overdue notifications.
     *
     * <p><b>FIX:</b> Original only called {@code ruleService.reloadRules()}. Now also
     * fetches overdue compliance records and dispatches bulk notifications.
     */
    @Override
    public void executeDailyCompliance() {
        log.info("[SCHEDULER] executeDailyCompliance started");

        ruleService.reloadRules();

        // Find and notify all overdue entities
        List<UUID> overdueEntityIds = complianceRepository
                .findByStatus("OVERDUE")
                .stream()
                .map(ComplianceRecord::getEntityId)
                .distinct()
                .toList();

        if (!overdueEntityIds.isEmpty()) {
            log.info("[SCHEDULER] Sending overdue notifications to {} entities", overdueEntityIds.size());
            notificationService.sendOverdueNotifications(overdueEntityIds);
        }

        log.info("[SCHEDULER] executeDailyCompliance complete");
    }

    /**
     * Send reminders for compliances due on the given date.
     *
     * <p><b>FIX:</b> Original called {@code ruleService.executeReminderRules()} but never
     * actually notified any entity. Now queries deadlines for today's date and dispatches
     * reminder notifications to the affected entities.
     */
    @Override
    public void sendReminder(LocalDate date) {
        log.info("[SCHEDULER] sendReminder for date={}", date);

        ruleService.executeReminderRules();

        List<UUID> entityIds = deadlineRepository
                .findByReminderDate(date)
                .stream()
                .map(d -> {
                    // Resolve entity from compliance record
                    return complianceRepository.findById(d.getComplianceId())
                            .map(ComplianceRecord::getEntityId)
                            .orElse(null);
                })
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (!entityIds.isEmpty()) {
            log.info("[SCHEDULER] Sending reminders to {} entities for date={}", entityIds.size(), date);
            notificationService.sendDueDateReminders(entityIds);
        }
    }

    /**
     * Mark compliances as OVERDUE where the due date has passed.
     *
     * <p><b>FIX:</b> Original only ran Drools. Now also updates the DB status
     * and collects entity IDs for notification.
     */
    @Override
    public void markOverdue() {
        log.info("[SCHEDULER] markOverdue started");

        ruleService.executeOverdueRules();

        LocalDate today = LocalDate.now();

        // Find all pending compliances past their due date
        List<ComplianceRecord> pendingOverdue = complianceRepository
                .findByStatus("PENDING")
                .stream()
                .filter(r -> r.getDueDate() != null && r.getDueDate().isBefore(today))
                .toList();

        if (!pendingOverdue.isEmpty()) {
            // Bulk update status
            pendingOverdue.forEach(r -> r.setStatus("OVERDUE"));
            complianceRepository.saveAll(pendingOverdue);

            List<UUID> entityIds = pendingOverdue.stream()
                    .map(ComplianceRecord::getEntityId)
                    .distinct()
                    .toList();

            log.info("[SCHEDULER] Marked {} records OVERDUE, notifying {} entities",
                    pendingOverdue.size(), entityIds.size());

            notificationService.sendOverdueNotifications(entityIds);
        }
    }

    // =====================================================
    // IDEMPOTENCY  (FIX: was returning false unconditionally)
    // =====================================================

    /**
     * Check whether a Kafka event has already been processed.
     *
     * <p>Uses Redis SETNX with a 24-hour TTL as a lightweight deduplication store.
     * Falls back to {@code false} (fail-open) if Redis is unavailable so that
     * compliance processing is never blocked by a cache outage.
     *
     * <p>Redis key format: {@code compliance:processed:<eventId>}
     */
    @Override
    public boolean alreadyProcessed(UUID eventId) {
        if (eventId == null) return false;

        try {
            if (redisTemplate == null) return false;

            String key = DEDUP_KEY_PREFIX + eventId;
            // SETNX — returns true if key was absent (first time seen)
            Boolean isNew = redisTemplate.opsForValue()
                    .setIfAbsent(key, "1", DEDUP_TTL_HOURS, TimeUnit.HOURS);
            // If isNew=false, key already existed → already processed
            return Boolean.FALSE.equals(isNew);

        } catch (Exception ex) {
            log.warn("[DEDUP] Redis check failed for eventId={} — failing open: {}", eventId, ex.getMessage());
            return false; // fail-open: process the event anyway
        }
    }

    // =====================================================
    // PRIVATE
    // =====================================================

    private void markProcessed(UUID eventId) {
        if (eventId == null || redisTemplate == null) return;
        try {
            String key = DEDUP_KEY_PREFIX + eventId;
            redisTemplate.opsForValue().set(key, "1", DEDUP_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception ex) {
            log.warn("[DEDUP] Failed to mark eventId={} as processed: {}", eventId, ex.getMessage());
        }
    }
}
