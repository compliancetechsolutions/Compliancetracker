package com.compliance.compliance.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.compliance.dto.RuleResultDto;
import com.compliance.compliance.entity.ComplianceRule;
import com.compliance.compliance.service.ComplianceRuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API for compliance rules management.
 *
 * <p><b>FIX:</b> Original was an empty stub class. Fully implemented.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/compliance/rules")
@RequiredArgsConstructor
public class ComplianceRuleController {

    private final ComplianceRuleService ruleService;

    // =====================================================
    // CRUD
    // =====================================================

    @PostMapping
    public ResponseEntity<ComplianceRule> create(@RequestBody ComplianceRule rule) {
        log.info("[API] POST /rules ruleName={}", rule.getRuleName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ruleService.create(rule));
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<ComplianceRule> getById(@PathVariable UUID ruleId) {
        return ResponseEntity.ok(ruleService.getById(ruleId));
    }

    @GetMapping
    public ResponseEntity<List<ComplianceRule>> getAll(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String countryCode) {

        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(ruleService.getActiveRules());
        }
        if (entityType != null) {
            return ResponseEntity.ok(ruleService.getByEntityType(entityType));
        }
        if (countryCode != null) {
            return ResponseEntity.ok(ruleService.getByCountry(countryCode));
        }
        return ResponseEntity.ok(ruleService.getAll());
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<ComplianceRule> update(
            @PathVariable UUID ruleId,
            @RequestBody ComplianceRule rule) {
        return ResponseEntity.ok(ruleService.update(ruleId, rule));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> delete(@PathVariable UUID ruleId) {
        ruleService.delete(ruleId);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // ACTIVATION
    // =====================================================

    @PutMapping("/{ruleId}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID ruleId) {
        ruleService.activate(ruleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ruleId}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID ruleId) {
        ruleService.deactivate(ruleId);
        return ResponseEntity.ok().build();
    }

    // =====================================================
    // EXECUTION
    // =====================================================

    @PostMapping("/{ruleId}/execute/{complianceId}")
    public ResponseEntity<RuleResultDto> executeRule(
            @PathVariable UUID ruleId,
            @PathVariable UUID complianceId) {
        log.info("[API] POST /rules/{}/execute/{}", ruleId, complianceId);
        return ResponseEntity.ok(ruleService.executeRule(complianceId, ruleId));
    }

    @PostMapping("/execute-all/{complianceId}")
    public ResponseEntity<List<RuleResultDto>> executeAllRules(@PathVariable UUID complianceId) {
        log.info("[API] POST /rules/execute-all/{}", complianceId);
        return ResponseEntity.ok(ruleService.executeAllRules(complianceId));
    }
}