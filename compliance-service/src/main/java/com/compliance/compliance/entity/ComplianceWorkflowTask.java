package com.compliance.compliance.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tracks individual workflow steps for a compliance record.
 *
 * <p><b>FIX:</b> Original was an empty stub. Populated with minimal fields
 * needed by {@link com.compliance.compliance.repository.WorkflowRepository}
 * and the WorkflowConsumer.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compliance_workflow_tasks", schema = "compliance_schema")
public class ComplianceWorkflowTask {

    @Id
    @Column(name = "workflow_task_id")
    private UUID workflowTaskId;

    @Column(name = "compliance_id", nullable = false)
    private UUID complianceId;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "workflow_id")
    private String workflowId;

    @Column(name = "task_name")
    private String taskName;

    /** PENDING / IN_PROGRESS / COMPLETED / FAILED */
    @Column(name = "task_status")
    private String taskStatus;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
