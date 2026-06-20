package com.compliance.common.kafka.outbox;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events", schema = "event_schema", indexes = {
    @Index(name = "idx_outbox_status_created", columnList = "status, createdAt") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "topic_name", nullable = false)
  private String topicName;

  /** Kafka message key — usually aggregateId.toString() */
  @Column(name = "message_key", nullable = false)
  private String messageKey;

  @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
  private String payload;

  @Builder.Default
  @Column(name = "status", nullable = false)
  private String status = "PENDING"; // PENDING | SENT | FAILED

  @Builder.Default
  @Column(name = "retry_count", nullable = false)
  private Integer retryCount = 0;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @PrePersist
  public void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (status == null) {
      status = "PENDING";
    }
  }
}