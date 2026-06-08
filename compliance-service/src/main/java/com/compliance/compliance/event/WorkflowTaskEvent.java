
package com.compliance.compliance.event;

import java.time.LocalDateTime;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.experimental.SuperBuilder;

import com.compliance.common.kafka.event.BaseEvent;

@Getter
@Setter

@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)

public class WorkflowTaskEvent

		extends BaseEvent {

// ========================================
// WORKFLOW
// ========================================

	private UUID workflowTaskId;

	private String workflowId;

// ========================================
// COMPLIANCE
// ========================================

	private UUID complianceId;

	private UUID entityId;

	private UUID ruleId;

// ========================================
// TASK
// ========================================

	private String taskName;

	private String taskStatus;

	private String action;

// ========================================
// EXECUTION
// ========================================

	private Boolean successful;

	private Integer retryCount;

// ========================================
// TIMESTAMP
// ========================================

	private LocalDateTime startedAt;

	private LocalDateTime completedAt;

// ========================================
// DETAILS
// ========================================

	private String remarks;

}
