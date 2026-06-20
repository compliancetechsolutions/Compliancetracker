package com.compliance.notification.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.compliance.enums.NotificationChannel;
import com.compliance.enums.NotificationStatus;
import com.compliance.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Table(

    name = "notification_log",

    schema = "notification_schema",

    indexes = {

        @Index(name = "idx_notification_log_event", columnList = "event_id"),

        @Index(name = "idx_notification_log_entity", columnList = "entity_id"),

        @Index(name = "idx_notification_log_status", columnList = "status")

    }

)

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

// ====================================
// ID
// ====================================

  @Id
  @GeneratedValue
  private UUID id;

// ====================================
// EVENT
// ====================================

  @Column(name = "event_id", nullable = false)

  private UUID eventId;
  
  // ====================================
  // ENTITY
  // ====================================

  @Column(name = "entity_id")

  private UUID entityId;

// ====================================
// AGGREGATE
// ====================================

  @Column(name = "aggregate_id")
  private UUID aggregateId;

// ====================================
// RECIPIENT
// ====================================

  @Column(name = "recipient")
  private String recipient;

// ====================================
// CHANNEL
// ====================================

  @Enumerated(EnumType.STRING)

  @Column(name = "channel")

  private NotificationChannel channel;

// ====================================
// TYPE
// ====================================

  @Enumerated(EnumType.STRING)

  @Column(name = "type")

  private NotificationType type;

// ====================================
// STATUS
// ====================================

  @Enumerated(EnumType.STRING)

  @Column(name = "status")

  private NotificationStatus status;

// ====================================
// MESSAGE
// ====================================

  @Column(name = "message", columnDefinition = "TEXT")

  private String message;

// ====================================
// ERROR
// ====================================

  @Column(name = "error_message", columnDefinition = "TEXT")

  private String errorMessage;

// ====================================
// RETRY
// ====================================

  @Column(name = "retry_count")

  @Builder.Default
  private Integer retryCount = 0;

// ====================================
// CREATED
// ====================================

  @Column(name = "created_at")

  @Builder.Default
  private OffsetDateTime createdAt =

      OffsetDateTime.now();

}