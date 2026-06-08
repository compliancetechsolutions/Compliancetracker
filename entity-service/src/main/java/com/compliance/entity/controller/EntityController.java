package com.compliance.entity.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.common.controller.BaseController;
import com.compliance.common.dto.ApiResponse;
import com.compliance.common.exception.ResourceNotFoundException;
import com.compliance.entity.dto.BulkEntityRequest;
import com.compliance.entity.dto.EntityBulkUserMappingRequest;
import com.compliance.entity.dto.EntityRequest;
import com.compliance.entity.dto.EntityResponse;
import com.compliance.entity.dto.EntityUserMappingRequest;
import com.compliance.entity.service.EntityService;
import com.compliance.common.enums.EntityErrorCode;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Entities", description = "Manage regulated entities")
@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntityController extends BaseController {

	private final EntityService entityService;

	// =====================================================
	// CREATE
	// =====================================================

	@Operation(summary = "Create entity")
	@PostMapping
	public ResponseEntity<ApiResponse<EntityResponse>> create(@Valid @RequestBody EntityRequest request) {

		EntityResponse created = entityService.create(request);

		return created("Entity created successfully", created);
	}
	
	
	@PostMapping("/bulk")
	public ResponseEntity<Void> createEntities(
	        @RequestBody BulkEntityRequest request) {

	    entityService.createEntities(request);

	    return ResponseEntity.ok().build();
	}
	

	// =====================================================
	// GET BY ID
	// =====================================================

	@Operation(summary = "Get entity by ID")
	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<EntityResponse>> get(
			@Parameter(description = "Entity UUID") @PathVariable UUID id) {

		EntityResponse response = entityService.getById(id)

				.orElseThrow(() -> new ResourceNotFoundException(EntityErrorCode.ENTITY_NOT_FOUND, id));

		return ok(response);
	}

	// =====================================================
	// GET ALL
	// =====================================================

	@Operation(summary = "List all entities")
	@GetMapping
	public ResponseEntity<ApiResponse<Page<EntityResponse>>> getAll(
			@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

		return ok(entityService.getAll(pageable));
	}

	// =====================================================
	// CURRENT USER ENTITIES
	// =====================================================

	@Operation(summary = "Entities mapped to current user")
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<EntityResponse>>> getMyEntities() {

		return ok(entityService.getMyEntities());
	}

	// =====================================================
	// MAP USER TO ENTITY
	// =====================================================

	@Operation(summary = "Map user to existing entity")
	@PostMapping("/{entityId}/users")
	public ResponseEntity<ApiResponse<Void>> mapUser(@PathVariable UUID entityId,
			@Valid @RequestBody EntityUserMappingRequest request) {

		entityService.mapUser(entityId, request);

		return ok("User mapped successfully");
	}
	// =====================================================
	// MAP Bulk USERS TO ENTITY
	// =====================================================

	@PostMapping("/{entityId}/users/bulk")
	public ResponseEntity<Void> mapUsers(@PathVariable UUID entityId,
			@Valid @RequestBody EntityBulkUserMappingRequest request) {

		entityService.mapUsers(entityId, request);

		return ResponseEntity.ok().build();
	}

	// =====================================================
	// INVESTOR VIEW
	// =====================================================

	@Operation(summary = "Investor entities")
	@GetMapping("/investor")
	public ResponseEntity<ApiResponse<Page<EntityResponse>>> getInvestorEntities(
			@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

		return ok(entityService.getInvestorEntities(pageable));
	}

	// =====================================================
	// REPRESENTATIVE VIEW
	// =====================================================

	@Operation(summary = "Representative entities")
	@GetMapping("/representative")
	public ResponseEntity<ApiResponse<Page<EntityResponse>>> getRepresentativeEntities(
			@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

		return ok(entityService.getRepresentativeEntities(pageable));
	}

	// =====================================================
	// UPDATE
	// =====================================================

	@Operation(summary = "Update entity")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<EntityResponse>> update(@PathVariable UUID id,
			@Valid @RequestBody EntityRequest request) {

		return ok("Entity updated successfully", entityService.update(id, request));
	}

	// =====================================================
	// DELETE
	// =====================================================

	@Operation(summary = "Delete entity")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {

		entityService.delete(id);

		return ok("Entity deleted successfully");
	}
}