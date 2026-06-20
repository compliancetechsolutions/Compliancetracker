package com.compliance.notification.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import com.compliance.enums.NotificationChannel;
import com.compliance.enums.NotificationStatus;
import com.compliance.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "notification_record",
    schema = "notification_schema",
    indexes = {
        @Index(name = "idx_notification_event",    columnList = "event_id"),
        @Index(name = "idx_notification_status",   columnList = "status"),
        @Index(name = "idx_notification_schedule", columnList = "scheduled_at"),
        @Index(name = "idx_notification_user",     columnList = "user_id"),
        @Index(name = "idx_notification_retry",    columnList = "status, retry_count, updated_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecord {

    // ── PK ──────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue
    private UUID id;

    // ── Event idempotency key ────────────────────────────────────────────────

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    // ── Domain refs ──────────────────────────────────────────────────────────

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "user_id")
    private UUID userId;

    // ── Delivery target ───────────────────────────────────────────────────────

    @Column(name = "recipient")
    private String recipient;

    // ── Channel / type ────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    // ── Content ───────────────────────────────────────────────────────────────

    @Column(name = "title")
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    // ── Audit timestamps (UTC, timezone-aware) ────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    // ── Optimistic lock ───────────────────────────────────────────────────────

    @Version
    private Long version;

    // ── JPA hooks ─────────────────────────────────────────────────────────────

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
