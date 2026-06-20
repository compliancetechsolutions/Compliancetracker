package com.compliance.notification.repository;

import java.time.OffsetDateTime;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.compliance.common.repository.BaseRepository;
import com.compliance.enums.NotificationStatus;
import com.compliance.notification.entity.NotificationRecord;

import jakarta.transaction.Transactional;

public interface NotificationRepository extends BaseRepository<NotificationRecord, UUID> {

    // ── Idempotency ──────────────────────────────────────────────────────────

    boolean existsByEventId(UUID eventId);

    Optional<NotificationRecord> findByEventId(UUID eventId);

    // ── Status queries (paginated — safe for 300M rows) ──────────────────────

    Page<NotificationRecord> findByStatus(NotificationStatus status, Pageable pageable);

    // ── Scheduler: retry FAILED (bounded) ────────────────────────────────────

    @Query("""
            SELECT n FROM NotificationRecord n
            WHERE n.status = :status
            AND n.retryCount < :maxRetries
            ORDER BY n.createdAt ASC
            """)
    Page<NotificationRecord> findRetryable(
            @Param("status") NotificationStatus status,
            @Param("maxRetries") int maxRetries,
            Pageable pageable);

    // ── Scheduler: DEAD-letter retry (bounded + retryCount cap) ──────────────

    @Query("""
            SELECT n FROM NotificationRecord n
            WHERE n.status = :status
            AND n.retryCount < :maxRetries
            ORDER BY n.updatedAt ASC
            """)
    Page<NotificationRecord> findDeadRetryable(
            @Param("status") NotificationStatus status,
            @Param("maxRetries") int maxRetries,
            Pageable pageable);

    // ── Scheduler: scheduled notifications (paginated, bounded) ─────────────

    @Query("""
            SELECT n FROM NotificationRecord n
            WHERE n.status = :status
            AND n.scheduledAt <= :now
            ORDER BY n.scheduledAt ASC
            """)
    Page<NotificationRecord> findScheduledNotifications(
            @Param("now") OffsetDateTime now,
            @Param("status") NotificationStatus status,
            Pageable pageable);

    // ── Cleanup (bulk DELETE — no load into heap) ─────────────────────────────

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM NotificationRecord n
            WHERE n.status = 'SENT'
            AND n.sentAt < :cutoff
            """)
    void deleteOldNotifications(@Param("cutoff") OffsetDateTime cutoff);

    // ── REST API helpers ──────────────────────────────────────────────────────

    Page<NotificationRecord> findByAggregateId(UUID aggregateId, Pageable pageable);

    Page<NotificationRecord> findByUserId(UUID userId, Pageable pageable);
}
