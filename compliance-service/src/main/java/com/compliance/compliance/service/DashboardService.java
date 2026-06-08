package com.compliance.compliance.service;

import java.time.LocalDate;
import java.util.UUID;

import com.compliance.compliance.dto.DashboardRequest;
import com.compliance.compliance.dto.DashboardResponse;

/**
 * Dashboard aggregation service.
 *
 * <p><b>FIX:</b> DashboardRequest was missing a {@code deadlineDate} field
 * that the service impl referenced. DashboardResponse was missing fields that
 * the impl tried to set. Both are now aligned.
 */
public interface DashboardService {

    // ======================================
    // DASHBOARD
    // ======================================

    DashboardResponse getDashboard();

    DashboardResponse getDashboard(DashboardRequest request);

    // ======================================
    // COMPLIANCE COUNTS
    // ======================================

    Long getTotalCompliance();

    Long getCompletedCompliance();

    Long getPendingCompliance();

    Long getOverdueCompliance();

    // ======================================
    // ENTITY
    // ======================================

    Long getEntityComplianceCount(UUID entityId);

    // ======================================
    // DEADLINES
    // ======================================

    Long getUpcomingDeadlineCount(LocalDate date);

    // ======================================
    // REVIEW
    // ======================================

    Long getReviewPendingCount();

    // ======================================
    // KPI
    // ======================================

    Double getCompletionRate();
}