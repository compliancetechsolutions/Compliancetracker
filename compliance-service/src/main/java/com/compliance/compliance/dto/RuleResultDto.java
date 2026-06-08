
package com.compliance.compliance.dto;

import java.time.LocalDateTime;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@Builder

@NoArgsConstructor
@AllArgsConstructor

public class RuleResultDto {

// ======================================
// IDS
// ======================================

	private UUID complianceId;

	private UUID ruleId;

	private UUID entityId;

// ======================================
// RESULT
// ======================================

	private Boolean success;

	private Boolean compliant;

	private Boolean overdue;

// ======================================
// RULE
// ======================================

	private String ruleName;

	private String ruleResult;

	private String message;

// ======================================
// EXECUTION
// ======================================

	private Integer firedRuleCount;

	private Long executionTimeMs;

// ======================================
// TIME
// ======================================

	private LocalDateTime executedAt;

}
