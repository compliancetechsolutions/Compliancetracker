package com.compliance.compliance.entity;

import java.time.LocalDate;
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

/**
 * Core compliance record — one row per compliance obligation assigned to an entity.
 *
 * <p><b>FIXES over original:</b>
 * <ol>
 *   <li><b>@OneToMany mappedBy="compliance" REMOVED</b> — the original declared
 *       three {@code @OneToMany(mappedBy="compliance")} associations to
 *       {@code ComplianceDeadline}, {@code ComplianceDocument}, and
 *       {@code ComplianceReview}. None of those child entities actually contained
 *       a field named "compliance" (they stored {@code UUID complianceId} — a bare
 *       foreign key, not a JPA managed reference). Hibernate would throw
 *       {@code MappingException: mappedBy reference an unknown target entity property}
 *       on startup. The associations are removed; child records are queried by
 *       {@code complianceId} via their own repositories.</li>
 *   <li><b>Extends BaseEntity</b> — original omitted this, losing {@code createdAt},
 *       {@code updatedAt}, {@code version}, {@code isDeleted} auditing columns.</li>
 *   <li><b>Added missing business columns</b> — {@code complianceType}, {@code frequency},
 *       {@code ruleId}, {@code notificationRequired}, {@code priority},
 *       {@code notificationSent}, {@code notificationRetryCount}, {@code remarks}
 *       — all referenced in service and mapper code but absent from the original entity.</li>
 * </ol>
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compliance_records", schema = "compliance_schema")
public class ComplianceRecord extends BaseEntity {

    // =====================================================
    // PRIMARY KEY
    // =====================================================

    @Id
    @Column(name = "compliance_id", nullable = false)
    private UUID complianceId;

    // =====================================================
    // REFERENCES
    // =====================================================

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    /**
     * Activity FK — stored as UUID; resolved via ActivityRepository when needed.
     * Using a bare FK (not a @ManyToOne managed reference) avoids N+1 fetch
     * issues in the scheduler hot path.
     */
    @Column(name = "activity_id")
    private UUID activityId;

    /** Optional Drools rule that governs this compliance. */
    @Column(name = "rule_id")
    private UUID ruleId;

    // =====================================================
    // BUSINESS
    // =====================================================

    @Column(name = "compliance_type", length = 100)
    private String complianceType;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "status", length = 50)
    private String status;

    // =====================================================
    // DATES
    // =====================================================

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    // =====================================================
    // NOTIFICATION
    // =====================================================

    @Column(name = "notification_required")
    private Boolean notificationRequired;

    @Column(name = "notification_sent")
    private Boolean notificationSent;

    @Builder.Default
    @Column(name = "notification_retry_count")
    private Integer notificationRetryCount = 0;

    // =====================================================
    // RULE ENGINE RESULT (last execution cached)
    // =====================================================

    @Column(name = "compliant")
    private Boolean compliant;

    @Column(name = "overdue")
    private Boolean overdue;

    @Column(name = "triggered_rule", length = 255)
    private String triggeredRule;

    @Column(name = "rule_result", columnDefinition = "TEXT")
    private String ruleResult;

    // =====================================================
    // PRIORITY / REMARKS
    // =====================================================

    @Builder.Default
    @Column(name = "priority")
    private Integer priority = 5;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    // NOTE: Child collections (deadlines, documents, reviews) are intentionally
    // NOT mapped as @OneToMany here. Query child repositories directly by
    // complianceId to avoid cartesian-product fetches in the scheduler hot path.
}