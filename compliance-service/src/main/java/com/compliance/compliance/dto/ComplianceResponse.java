
package com.compliance.compliance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@Builder

@NoArgsConstructor
@AllArgsConstructor

public class ComplianceResponse {


// =====================================================
// IDENTIFIERS
// =====================================================

private UUID complianceId;

private UUID entityId;

private UUID activityId;

private UUID executionId;

private UUID ruleId;



// =====================================================
// BUSINESS
// =====================================================

private String complianceType;

private String frequency;

private String status;



// =====================================================
// DATES
// =====================================================

private LocalDate dueDate;

private LocalDate completedDate;

private LocalDate effectiveDate;



// =====================================================
// RULE ENGINE
// =====================================================

private Boolean compliant;

private Boolean overdue;

private String triggeredRule;

private String ruleResult;



// =====================================================
// NOTIFICATION
// =====================================================

private Boolean notificationSent;

private Integer notificationRetryCount;



// =====================================================
// AUDIT
// =====================================================

private LocalDateTime createdAt;

private LocalDateTime updatedAt;

private UUID createdBy;



// =====================================================
// MESSAGE
// =====================================================

private String message;

}

