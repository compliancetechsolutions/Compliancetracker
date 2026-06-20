package com.compliance.notification.serviceimpl;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.enums.NotificationStatus;
import com.compliance.notification.dto.NotificationRequest;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.mapper.NotificationMapper;
import com.compliance.notification.repository.NotificationRepository;
import com.compliance.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    /**
     * Idempotent create: if the eventId already exists, return existing ID (no-op).
     * Never throws on duplicate — callers (Kafka consumer) just ack and move on.
     */
    @Override
    @Transactional
    public UUID create(NotificationRequest request) {
        // ── Idempotency guard ─────────────────────────────────────────────────
        return notificationRepository.findByEventId(request.getEventId())
                .map(existing -> {
                    log.warn("[Notification] Duplicate eventId={} — returning existing id={}",
                            request.getEventId(), existing.getId());
                    return existing.getId();
                })
                .orElseGet(() -> {
                    NotificationRecord record = notificationMapper.toEntity(request);
                    record.setStatus(NotificationStatus.PENDING);
                    record.setRetryCount(0);
                    NotificationRecord saved = notificationRepository.save(record);
                    log.info("[Notification] Created id={} channel={} eventId={}",
                            saved.getId(), saved.getChannel(), saved.getEventId());
                    return saved.getId();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationRecord findById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationRecord> findByStatus(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationRecord> findByUserId(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void markSent(UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setStatus(NotificationStatus.SENT);
            n.setSentAt(OffsetDateTime.now());
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markFailed(UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRetryCount(n.getRetryCount() + 1);
            n.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markDead(UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRetryCount(n.getRetryCount() + 1);
            n.setStatus(NotificationStatus.DEAD);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markExhausted(UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setStatus(NotificationStatus.EXHAUSTED);
            notificationRepository.save(n);
        });
    }
}
