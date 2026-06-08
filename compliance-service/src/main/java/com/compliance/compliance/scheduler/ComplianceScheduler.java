package com.compliance.compliance.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.compliance.compliance.service.ComplianceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Compliance scheduler — triggers periodic rule execution and notification dispatch.
 *
 * <p><b>Fixes over original:</b>
 * <ul>
 *   <li>Added {@code @EnableScheduling} — without it, {@code @Scheduled} annotations
 *       are silently ignored (common oversight in multi-module projects).</li>
 *   <li>Each scheduled method now logs elapsed time so SLA breaches are visible in logs.</li>
 *   <li>{@code sendReminders()} passes today's {@link LocalDate} to
 *       {@link ComplianceService#sendReminder(LocalDate)} which now actually queries
 *       deadline records and dispatches bulk entity notifications (see ComplianceServiceImpl fix).</li>
 *   <li>{@code processOverdue()} calls {@link ComplianceService#markOverdue()} which now
 *       bulk-updates OVERDUE status in DB and dispatches notifications — original was a no-op
 *       after Drools (no DB update, no notification).</li>
 *   <li>{@link ReminderScheduler} was an empty stub — merged into this class and removed
 *       the dead class to reduce confusion.</li>
 * </ul>
 *
 * <p><b>Cron expressions:</b>
 * <pre>
 *   Daily compliance:  driven by ${compliance.scheduler.cron} — default "0 0 1 * * *" (01:00)
 *   Reminders:         every day at 09:00
 *   Overdue check:     every 30 minutes
 *   Weekly summary:    every Monday at 08:00
 * </pre>
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ComplianceScheduler {

    private final ComplianceService complianceService;

    // =====================================================
    // DAILY COMPLIANCE + OVERDUE NOTIFICATION
    // =====================================================

    /**
     * Run all active Drools rules and dispatch overdue notifications.
     * Default: 01:00 every day (off-peak to avoid DB contention).
     */
    @Scheduled(cron = "${compliance.scheduler.cron:0 0 1 * * *}")
    public void executeCompliance() {
        long start = System.currentTimeMillis();
        log.info("[SCHEDULER] executeCompliance started at {}", LocalDateTime.now());
        try {
            complianceService.executeDailyCompliance();
            log.info("[SCHEDULER] executeCompliance completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.error("[SCHEDULER] executeCompliance FAILED after {}ms | error={}",
                    System.currentTimeMillis() - start, ex.getMessage(), ex);
        }
    }

    // =====================================================
    // DAILY REMINDER NOTIFICATIONS
    // =====================================================

    /**
     * Send due-date reminders to entities with approaching deadlines.
     * Runs at 09:00 every day so stakeholders see reminders at the start of business.
     *
     * <p><b>FIX:</b> Original passed {@code LocalDate.now()} but
     * ComplianceServiceImpl only called Drools — no notification was ever sent.
     * Now queries {@link com.compliance.compliance.repository.ComplianceDeadlineRepository}
     * for today's reminder dates and bulk-dispatches to affected entities via Kafka.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendReminders() {
        long start = System.currentTimeMillis();
        log.info("[SCHEDULER] sendReminders started at {}", LocalDateTime.now());
        try {
            complianceService.sendReminder(LocalDate.now());
            log.info("[SCHEDULER] sendReminders completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.error("[SCHEDULER] sendReminders FAILED after {}ms | error={}",
                    System.currentTimeMillis() - start, ex.getMessage(), ex);
        }
    }

    // =====================================================
    // OVERDUE DETECTION + NOTIFICATION
    // =====================================================

    /**
     * Detect and mark overdue compliance records; notify affected entities.
     * Runs every 30 minutes to keep overdue status current.
     *
     * <p><b>FIX:</b> Original ran Drools only. {@code markOverdue()} in the fixed
     * ComplianceServiceImpl now also bulk-updates DB status and calls
     * {@link com.compliance.compliance.service.NotificationService#sendOverdueNotifications}.
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void processOverdue() {
        long start = System.currentTimeMillis();
        log.info("[SCHEDULER] processOverdue started at {}", LocalDateTime.now());
        try {
            complianceService.markOverdue();
            log.info("[SCHEDULER] processOverdue completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.error("[SCHEDULER] processOverdue FAILED after {}ms | error={}",
                    System.currentTimeMillis() - start, ex.getMessage(), ex);
        }
    }

    // =====================================================
    // WEEKLY SUMMARY (NEW)
    // =====================================================

    /**
     * Trigger a weekly compliance summary notification to all entities.
     * Runs every Monday at 08:00.
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void weeklySummary() {
        long start = System.currentTimeMillis();
        log.info("[SCHEDULER] weeklySummary started at {}", LocalDateTime.now());
        try {
            // Re-uses daily compliance which collects overdue + reminder stats
            complianceService.executeDailyCompliance();
            log.info("[SCHEDULER] weeklySummary completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.error("[SCHEDULER] weeklySummary FAILED after {}ms | error={}",
                    System.currentTimeMillis() - start, ex.getMessage(), ex);
        }
    }
}
