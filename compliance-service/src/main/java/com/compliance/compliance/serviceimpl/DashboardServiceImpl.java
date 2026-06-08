package com.compliance.compliance.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.compliance.dto.DashboardRequest;
import com.compliance.compliance.dto.DashboardResponse;
import com.compliance.compliance.repository.ComplianceRepository;
import com.compliance.compliance.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dashboard aggregation service.
 *
 * <p><b>FIXES over original:</b>
 * <ol>
 *   <li><b>Builder method name mismatch fixed</b> — original called
 *       {@code .totalCompliance()} etc. but the DTO had {@code totalCompliances} (plural).
 *       DashboardResponse now uses singular names matching these call sites.</li>
 *   <li><b>getOverdueCompliance()</b> — original called
 *       {@code findOverdueEntityIds(today).stream().count()} which loads all UUIDs
 *       into memory just to count them. Fixed to use
 *       {@code countByDueDateBeforeAndStatus()} via a repository count query.</li>
 *   <li><b>getEntityComplianceCount()</b> — original called
 *       {@code findByEntityId().stream().count()} loading all records. Fixed to use
 *       {@code countByEntityId(entityId)} — single COUNT SQL query.</li>
 *   <li><b>generatedAt timestamp</b> — added so callers know when the snapshot was taken.</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ComplianceRepository complianceRepository;

    // =====================================================
    // DASHBOARD
    // =====================================================

    @Override
    public DashboardResponse getDashboard() {
        return DashboardResponse.builder()
                .totalCompliance(getTotalCompliance())
                .completedCompliance(getCompletedCompliance())
                .pendingCompliance(getPendingCompliance())
                .overdueCompliance(getOverdueCompliance())
                .reviewPending(getReviewPendingCount())
                .completionRate(getCompletionRate())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public DashboardResponse getDashboard(DashboardRequest request) {
        DashboardResponse response = getDashboard();

        if (request == null) return response;

        if (request.getEntityId() != null) {
            response.setEntityId(request.getEntityId());
            response.setEntityCompliance(getEntityComplianceCount(request.getEntityId()));
        }

        if (request.getDeadlineDate() != null) {
            response.setUpcomingDeadline(getUpcomingDeadlineCount(request.getDeadlineDate()));
        }

        return response;
    }

    // =====================================================
    // COMPLIANCE COUNTS
    // =====================================================

    @Override
    public Long getTotalCompliance() {
        return complianceRepository.count();
    }

    @Override
    public Long getCompletedCompliance() {
        return complianceRepository.countByStatus("COMPLETED");
    }

    @Override
    public Long getPendingCompliance() {
        return complianceRepository.countByStatus("PENDING");
    }

    /**
     * FIX: Use a COUNT DB query instead of loading all UUID objects into memory.
     */
    @Override
    public Long getOverdueCompliance() {
        return complianceRepository.countByDueDateBeforeAndStatus(LocalDate.now(), "PENDING")
             + complianceRepository.countByStatus("OVERDUE");
    }

    // =====================================================
    // ENTITY
    // =====================================================

    /**
     * FIX: Single COUNT query, no in-memory streaming.
     */
    @Override
    public Long getEntityComplianceCount(UUID entityId) {
        return complianceRepository.countByEntityId(entityId);
    }

    // =====================================================
    // DEADLINES
    // =====================================================

    @Override
    public Long getUpcomingDeadlineCount(LocalDate date) {
        return (long) complianceRepository
                .findUpcomingDeadlines(LocalDate.now(), date)
                .size();
    }

    // =====================================================
    // REVIEW
    // =====================================================

    @Override
    public Long getReviewPendingCount() {
        return complianceRepository.countByStatus("UNDER_REVIEW");
    }

    // =====================================================
    // KPI
    // =====================================================

    @Override
    public Double getCompletionRate() {
        long total = getTotalCompliance();
        if (total == 0) return 0.0;
        long completed = getCompletedCompliance();
        return Math.round(((completed * 100.0) / total) * 100.0) / 100.0;
    }
}