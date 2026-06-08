package com.compliance.compliance.event;


import java.time.LocalDate;
import java.util.UUID;
import com.compliance.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter

@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(
callSuper = true
)

public class ComplianceCompletedEvent

extends BaseEvent {


// ======================================
// REFERENCES
// ======================================

private UUID complianceId;

private UUID entityId;

private UUID activityId;

private UUID executionId;

private UUID ruleId;



// ======================================
// RESULT
// ======================================

private String status;

private Boolean compliant;

private Boolean overdue;



// ======================================
// RULE
// ======================================

private String triggeredRule;

private String ruleResult;



// ======================================
// DATES
// ======================================

private LocalDate dueDate;

private LocalDate completedDate;



// ======================================
// NOTIFICATION
// ======================================

private Boolean notificationSent;

private Integer retryCount;



// ======================================
// EXECUTION
// ======================================

private Long executionTimeMs;

private Integer firedRuleCount;



// ======================================
// EXTRA
// ======================================

private String remarks;

}

