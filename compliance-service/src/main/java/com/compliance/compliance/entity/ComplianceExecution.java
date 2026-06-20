package com.compliance.compliance.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Getter
@Setter

@Builder

@NoArgsConstructor
@AllArgsConstructor

@Table(name = "compliance_execution", schema = "compliance_schema")

public class ComplianceExecution

    extends BaseEntity {

// ========================================
// PRIMARY KEY
// ========================================

  @Id

  @Column(name = "execution_id")

  private UUID executionId;

// ========================================
// REFERENCES
// ========================================

  @Column(name = "compliance_id", nullable = false)

  private UUID complianceId;

  @Column(name = "rule_id")

  private UUID ruleId;

// ========================================
// EXECUTION
// ========================================

  @Column(name = "execution_status")

  private String executionStatus;

  @Column(name = "execution_result", columnDefinition = "TEXT")

  private String executionResult;

  @Column(name = "triggered_rule")

  private String triggeredRule;

  @Column(name = "execution_time_ms")

  private Long executionTimeMs;

  @Column(name = "fired_rule_count")

  private Integer firedRuleCount;

// ========================================
// FLAGS
// ========================================

  @Column(name = "success")

  private Boolean success;

  @Column(name = "notification_sent")

  private Boolean notificationSent;

// ========================================
// TIMESTAMP
// ========================================

  @Column(name = "executed_at")

  private LocalDateTime executedAt;

}
