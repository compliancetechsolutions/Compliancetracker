package com.compliance.compliance.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard query request.
 *
 * <p><b>FIX:</b> Added {@code deadlineDate} field which was referenced by
 * {@link com.compliance.compliance.serviceimpl.DashboardServiceImpl#getDashboard(DashboardRequest)}
 * via {@code request.getDeadlineDate()} but was absent from the original DTO,
 * causing a compile error.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequest {

    // =====================================================
    // FILTERS
    // =====================================================

    private UUID entityId;

    private UUID activityId;

    private UUID ruleId;

    // =====================================================
    // COMPLIANCE TYPE / STATUS
    // =====================================================

    private String complianceType;

    private String status;

    private String frequency;

    // =====================================================
    // DATE FILTER
    // =====================================================

    private LocalDate fromDate;

    private LocalDate toDate;

    /**
     * ADDED — referenced by DashboardServiceImpl.getDashboard(DashboardRequest)
     * to compute upcoming deadline count. Date up to which deadlines are considered
     * "upcoming".
     */
    private LocalDate deadlineDate;

    // =====================================================
    // DASHBOARD FLAGS
    // =====================================================

    @Builder.Default
    private Boolean includeCompleted = true;

    @Builder.Default
    private Boolean includeOverdue = true;

    @Builder.Default
    private Boolean includeNotifications = true;

    // =====================================================
    // PRIORITY
    // =====================================================

    private List<Integer> priorities;

    // =====================================================
    // PAGINATION
    // =====================================================

    @Builder.Default
    @Min(0)
    private Integer page = 0;

    @Builder.Default
    @Min(1)
    @Max(100)
    private Integer size = 20;

    // =====================================================
    // SORT
    // =====================================================

    @Builder.Default
    private String sortBy = "dueDate";

    @Builder.Default
    private String direction = "DESC";
}