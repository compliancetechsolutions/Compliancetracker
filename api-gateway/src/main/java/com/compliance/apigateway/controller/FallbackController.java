package com.compliance.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Circuit-breaker fallback responses.
 * Each route that has a fallbackUri points here.
 * Returns a structured payload that matches the platform's error schema.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return serviceUnavailable("auth-service");
    }

    @GetMapping("/entity")
    public ResponseEntity<Map<String, Object>> entityFallback() {
        return serviceUnavailable("entity-service");
    }

    @GetMapping("/compliance")
    public ResponseEntity<Map<String, Object>> complianceFallback() {
        return serviceUnavailable("compliance-service");
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        return serviceUnavailable("notification-service");
    }

    @GetMapping("/audit")
    public ResponseEntity<Map<String, Object>> auditFallback() {
        return serviceUnavailable("audit-service");
    }

    @GetMapping("/archive")
    public ResponseEntity<Map<String, Object>> archiveFallback() {
        return serviceUnavailable("archive-service");
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> reportFallback() {
        return serviceUnavailable("report-service");
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> serviceUnavailable(String service) {
        Map<String, Object> body = Map.of(
                "success",   false,
                "status",    503,
                "errorCode", "SVC_UNAVAILABLE",
                "message",   service + " is temporarily unavailable. Please retry shortly.",
                "timestamp", Instant.now().toString()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}