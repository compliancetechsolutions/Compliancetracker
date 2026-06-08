package com.compliance.compliance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.compliance.dto.DashboardRequest;
import com.compliance.compliance.dto.DashboardResponse;
import com.compliance.compliance.service.DashboardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dashboard REST API.
 *
 * <p><b>FIX:</b> Original was an empty stub class — no endpoints, no Spring annotations.
 * Fully implemented with GET (summary) and POST (filtered) endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard
     * Returns aggregate compliance counts and KPIs.
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        log.info("[API] GET /dashboard");
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    /**
     * POST /api/v1/dashboard
     * Returns filtered dashboard metrics (by entity, date range, deadline, etc.).
     */
    @PostMapping
    public ResponseEntity<DashboardResponse> getFilteredDashboard(
            @Valid @RequestBody DashboardRequest request) {
        log.info("[API] POST /dashboard entityId={} deadlineDate={}",
                request.getEntityId(), request.getDeadlineDate());
        return ResponseEntity.ok(dashboardService.getDashboard(request));
    }
}