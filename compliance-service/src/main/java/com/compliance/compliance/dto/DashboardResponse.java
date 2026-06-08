package com.compliance.compliance.dto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard response DTO.
 *
 * <p><b>FIXES over original:</b>
 * <ol>
 *   <li><b>Field name mismatch fixed</b> — original had {@code totalCompliances},
 *       {@code completedCompliances} etc. (plural "Compliances"), but
 *       {@link com.compliance.compliance.serviceimpl.DashboardServiceImpl} called
 *       builder methods {@code .totalCompliance()}, {@code .completedCompliance()}
 *       etc. (singular). Lombok builder generates methods from field names, so the
 *       mismatch caused compile errors. Fields are now singular to match the service.</li>
 *   <li><b>Missing fields added</b> — {@code reviewPending}, {@code entityCompliance},
 *       {@code upcomingDeadline} were referenced by the service but absent from the DTO.</li>
 *   <li><b>completionRate type changed</b> — was {@code BigDecimal} in the original
 *       but the service returns {@code Double}. Changed to {@code Double} to match.</li>
 * </ol>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // =====================================================
    // TOTAL COUNTS  (FIXED: singular field names to match DashboardServiceImpl)
    // =====================================================

    private Long totalCompliance;

    private Long completedCompliance;

    private Long pendingCompliance;

    private Long overdueCompliance;

    private Long reviewPending;

    // =====================================================
    // ENTITY-SPECIFIC (set only when DashboardRequest.entityId provided)
    // =====================================================

    private UUID entityId;

    private Long entityCompliance;

    // =====================================================
    // DEADLINES (set only when DashboardRequest.deadlineDate provided)
    // =====================================================

    private Long upcomingDeadline;

    // =====================================================
    // NOTIFICATIONS
    // =====================================================

    private Long totalNotifications;

    private Long notificationFailures;

    private Long notificationSuccess;

    // =====================================================
    // RULES
    // =====================================================

    private Long totalRulesExecuted;

    private Long passedRules;

    private Long failedRules;

    // =====================================================
    // KPI  (FIXED: Double to match service return type)
    // =====================================================

    private Double completionRate;

    private Double overdueRate;

    // =====================================================
    // TIMESTAMP
    // =====================================================

    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    // =====================================================
    // RECENT ACTIVITY
    // =====================================================

    private List<ActivitySummary> recentActivities;

    private DashboardSummary summary;

    // =====================================================
    // INNER DTOs
    // =====================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivitySummary {
        private String complianceType;
        private String status;
        private String rule;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummary {
        private Long todayCompleted;
        private Long todayPending;
        private Long todayNotifications;
        private Long todayOverdue;
    }
}