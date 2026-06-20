package com.compliance.common.kafka.event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Kafka event published to notify an entity (and its users) about a compliance
 * outcome.
 *
 * <p>
 * Consumed by any downstream notification micro-service (email, SMS, push,
 * in-app) and by entity-service to update entity-level notification counters.
 *
 * <p>
 * Key design decisions for 2-billion-notification scale:
 * <ul>
 * <li>Partitioned by {@code entityId.toString()} — keeps per-entity ordering
 * while spreading load across 200+ partitions.</li>
 * <li>All fields are nullable-safe: consumers must not fail on missing optional
 * data.</li>
 * <li>Inherits {@code eventId} from {@link BaseEvent} for idempotency
 * deduplication.</li>
 * </ul>
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {

  // =====================================================
  // TARGET
  // =====================================================

  /**
   * 
   */
  private static final long serialVersionUID = -2953530722263050732L;

  /** Entity to be notified — used as Kafka partition key. */
  private UUID entityId;

  /**
   * Optional specific user within the entity. Null → broadcast to all entity
   * admins.
   */
  private UUID userId;

  // =====================================================
  // COMPLIANCE CONTEXT
  // =====================================================

  private UUID complianceId;

  private UUID activityId;

  private UUID ruleId;

  // =====================================================
  // NOTIFICATION CONTENT
  // =====================================================

  /**
   * SHORT_CODE values: COMPLIANCE_DUE, COMPLIANCE_OVERDUE, COMPLIANCE_COMPLETED,
   * COMPLIANCE_CREATED, REMINDER, RULE_VIOLATION, WORKFLOW_UPDATE
   */
  private String notificationType;

  private String title;

  private String message;

  /** LOW / MEDIUM / HIGH / CRITICAL */
  private String priority;

  // =====================================================
  // DELIVERY CHANNELS
  // =====================================================

  /** When true, downstream service must send email. */
  private Boolean sendEmail;

  /** When true, downstream service must send push notification. */
  private Boolean sendPush;

  /** When true, downstream service must send SMS. */
  private Boolean sendSms;

  // =====================================================
  // RETRY / TRACKING
  // =====================================================

  private Integer retryCount;

  private LocalDateTime scheduledAt;

  // =====================================================
  // COMPLIANCE DETAILS (for enrichment)
  // =====================================================

  private String complianceType;

  private String complianceStatus;

  private Boolean compliant;

  private Boolean overdue;

  private LocalDate dueDate;
}
