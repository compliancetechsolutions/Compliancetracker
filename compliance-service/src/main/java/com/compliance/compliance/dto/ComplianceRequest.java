
package com.compliance.compliance.dto;

import java.time.LocalDate;

import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@Builder

@NoArgsConstructor
@AllArgsConstructor

public class ComplianceRequest {

// =====================================================
// ENTITY
// =====================================================

	@NotNull(message = "Entity Id required")

	private UUID entityId;

	@NotNull(message = "Activity Id required")

	private UUID activityId;

// =====================================================
// COMPLIANCE
// =====================================================

	@NotBlank(message = "Compliance type required")

	@Size(max = 100)

	private String complianceType;

	@NotBlank(message = "Frequency required")

	private String frequency;

	@NotBlank(message = "Status required")

	private String status;

// =====================================================
// DATES
// =====================================================

	@NotNull(message = "Due date required")

	@FutureOrPresent

	private LocalDate dueDate;

	private LocalDate effectiveDate;

// =====================================================
// RULE
// =====================================================

	private UUID ruleId;

// =====================================================
// NOTIFICATION
// =====================================================

	@Builder.Default

	private Boolean notificationRequired = true;

	@Builder.Default

	private Integer priority = 5;

// =====================================================
// EXTRA
// =====================================================

@Size(
max = 1000
)

private String remarks;

}
