package com.compliance.compliance.controller;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import com.compliance.compliance.dto.ComplianceRequest;
import com.compliance.compliance.dto.ComplianceResponse;
import com.compliance.compliance.service.ComplianceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API for compliance records.
 *
 * <p>
 * <b>FIX:</b> Original was an empty stub class. Fully implemented.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/compliance")
@RequiredArgsConstructor
public class ComplianceController {

	private final ComplianceService complianceService;

	// =====================================================
	// CREATE
	// =====================================================

	@PostMapping
	public ResponseEntity<ComplianceResponse> create(@Valid @RequestBody ComplianceRequest request) {
		log.info("[API] POST /compliance entityId={}", request.getEntityId());
		return ResponseEntity.status(HttpStatus.CREATED).body(complianceService.create(request));
	}

	// =====================================================
	// READ
	// =====================================================

	@GetMapping("/{id}")
	public ResponseEntity<ComplianceResponse> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(complianceService.getById(id));
	}

	@GetMapping
	public ResponseEntity<Page<ComplianceResponse>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "dueDate") String sort,
			@RequestParam(defaultValue = "DESC") String dir) {

		Sort.Direction direction = Sort.Direction.fromOptionalString(dir).orElse(Sort.Direction.DESC);
		PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sort));
		return ResponseEntity.ok(complianceService.getAll(pageable));
	}

	// =====================================================
	// UPDATE
	// =====================================================

	@PutMapping("/{id}")
	public ResponseEntity<ComplianceResponse> update(@PathVariable UUID id,
			@Valid @RequestBody ComplianceRequest request) {
		return ResponseEntity.ok(complianceService.update(id, request));
	}

	// =====================================================
	// DELETE
	// =====================================================

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		complianceService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// =====================================================
	// RULE EXECUTION
	// =====================================================

	@PostMapping("/{id}/execute-rules")
	public ResponseEntity<ComplianceResponse> executeRules(@PathVariable UUID id) {
		return ResponseEntity.ok(complianceService.executeRules(id));
	}
}
