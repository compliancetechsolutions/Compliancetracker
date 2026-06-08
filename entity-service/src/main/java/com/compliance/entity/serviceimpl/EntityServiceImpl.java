package com.compliance.entity.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.common.enums.AuthErrorCode;
import com.compliance.common.enums.EntityErrorCode;
import com.compliance.common.enums.EntityStatus;
import com.compliance.common.enums.Role;
import com.compliance.common.exception.BaseException;
import com.compliance.common.exception.ResourceNotFoundException;
import com.compliance.common.security.UserContext;
import com.compliance.common.serviceimpl.BaseServiceImpl;
import com.compliance.entity.dto.BulkEntityRequest;
import com.compliance.entity.dto.BulkEntityResponse;
import com.compliance.entity.dto.EntityBulkUserMappingRequest;
import com.compliance.entity.dto.EntityRequest;
import com.compliance.entity.dto.EntityResponse;
import com.compliance.entity.dto.EntityUserMappingRequest;
import com.compliance.entity.entity.EntityMaster;
import com.compliance.entity.entity.EntityUserMapper;
import com.compliance.entity.mapper.EntityMapper;
import com.compliance.entity.repository.EntityRepository;
import com.compliance.entity.repository.EntityUserMapperRepository;
import com.compliance.entity.service.EntityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EntityServiceImpl extends BaseServiceImpl<EntityRequest, EntityRequest, EntityResponse, EntityMaster, UUID>
		implements EntityService {

	private final EntityRepository entityRepo;
	private final EntityUserMapperRepository mapperRepo;
	private final EntityMapper mapper;
	

	
	public EntityServiceImpl(EntityRepository entityRepo, EntityUserMapperRepository mapperRepo, EntityMapper mapper) {

		super(entityRepo);
		this.entityRepo = entityRepo;
		this.mapperRepo = mapperRepo;
		this.mapper = mapper;
	}
		
	// =====================================================
	// CREATE (ADMIN ONLY)
	// =====================================================

	@Override
	@Transactional
	public EntityResponse create(EntityRequest request) {

		requireRole(Role.ADMIN);
		validateNoDuplicateName(request.getEntityName(), null);
		EntityMaster entity = mapper.toEntity(request);
		entity.setEntityId(UUID.randomUUID());
		entity.setCompanyStartDate(request.getCompanyStartDate());
		entity.setNoOfEmployees(request.getNoOfEmployees());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setVersion(0L);
		entity.setIsDeleted(false);
		EntityMaster saved = entityRepo.save(entity);
		log.info("Entity created id={} by admin={}", saved.getEntityId(), UserContext.getUserId());
		return mapper.toResponse(saved);
	}

	@Override
	@Transactional
	public BulkEntityResponse createEntities(BulkEntityRequest request) {

		requireRole(Role.ADMIN);

		List<EntityResponse> created = new ArrayList<>();

		List<String> skipped = new ArrayList<>();

		for (EntityRequest entityRequest : request.getEntities()) {

			boolean exists = entityRepo.existsByEntityNameIgnoreCaseAndIsDeletedFalse(entityRequest.getEntityName());

			if (exists) {

				skipped.add(entityRequest.getEntityName());

				continue;
			}

			EntityMaster entity = EntityMaster.builder()

					.entityId(UUID.randomUUID()).entityName(entityRequest.getEntityName())
					.entityTypeId(entityRequest.getEntityTypeId())
					.registrationNumber(entityRequest.getRegistrationNumber())
					.companyStartDate(entityRequest.getCompanyStartDate())
					.noOfEmployees(entityRequest.getNoOfEmployees()).build();

			entity.setVersion(0L);

			entity.setIsDeleted(false);

			entityRepo.save(entity);

			created.add(

					EntityResponse.builder()

							.entityId(entity.getEntityId()).entityName(entity.getEntityName())
							.entityTypeId(entity.getEntityTypeId()).registrationNumber(entity.getRegistrationNumber())
							.status(entity.getStatus().name()).companyStartDate(entity.getCompanyStartDate())
							.noOfEmployees(entity.getNoOfEmployees()).createdAt(entity.getCreatedAt())
							.updatedAt(entity.getUpdatedAt()).createdBy(entity.getCreatedBy()).build());
		}

		return BulkEntityResponse.builder().totalRequested(request.getEntities().size()).createdCount(created.size())
				.skippedCount(skipped.size()).createdEntities(created).skippedEntities(skipped)
				.message("Bulk entity creation completed successfully").build();
	}

	// UPDATE
	// =====================================================

	@Override
	public EntityResponse update(UUID entityId, EntityRequest request) {

		EntityMaster entity = fetchWithAccessCheck(entityId);

		updateNameIfNeeded(entity, request);
		updateIfPresent(request.getRegistrationNumber(), entity::setRegistrationNumber);

		updateIfPresent(request.getNoOfEmployees(), entity::setNoOfEmployees);

		updateIfPresent(request.getCompanyStartDate(), entity::setCompanyStartDate);

		EntityMaster updated = entityRepo.save(entity);

		log.info("Entity updated id={} by user={}", entityId, UserContext.getUserId());

		return mapper.toResponse(updated);

	}

	// =====================================================
	// GET BY ID
	// =====================================================

	@Override
	@Transactional(readOnly = true)
	public Optional<EntityResponse> getById(UUID entityId) {

		return Optional.of(mapper.toResponse(fetchWithAccessCheck(entityId)));
	}

	// =====================================================
	// GET ALL (ADMIN ONLY)
	// =====================================================

	@Override
	@Transactional(readOnly = true)
	public Page<EntityResponse> getAll(Pageable pageable) {

		requireRole(Role.ADMIN);

		return entityRepo.findAll(withDefaultSort(pageable)).map(mapper::toResponse);
	}

	// =====================================================
	// DELETE (SOFT DELETE)
	// =====================================================

	@Override
	public void delete(UUID entityId) {

		requireRole(Role.ADMIN);

		EntityMaster entity = fetchWithAccessCheck(entityId);
		entity.setIsDeleted(true);

		entityRepo.save(entity);

		log.info("Entity deleted id={} by user={}", entityId, UserContext.getUserId());
	}

	// =====================================================
	// CUSTOM METHODS
	// =====================================================

	@Override
	@Transactional(readOnly = true)
	public List<EntityResponse> getMyEntities() {

		return entityRepo.findByUserId(UserContext.getUserId()).stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EntityResponse> getInvestorEntities(Pageable pageable) {

		requireRole(Role.INVESTOR);

		return entityRepo.findInvestorEntities(UserContext.getUserId(), withDefaultSort(pageable))
				.map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EntityResponse> getRepresentativeEntities(Pageable pageable) {

		requireRole(Role.COMPANY_REPRESENTATIVE);

		return entityRepo.findRepresentativeEntities(UserContext.getUserId(), withDefaultSort(pageable))
				.map(mapper::toResponse);
	}
	// =====================================================
	// SECURITY
	// =====================================================

	private void requireRole(Role minimumRole) {

		if (!UserContext.hasMinimumRole(minimumRole)) {
			throw new BaseException(AuthErrorCode.AUTH_FORBIDDEN, "Access denied");
		}
	}

	private EntityMaster fetchWithAccessCheck(UUID entityId) {

		boolean isAdmin = UserContext.hasMinimumRole(Role.ADMIN);

		if (!isAdmin) {
			boolean mapped = mapperRepo.existsByUserIdAndEntityIdAndIsDeletedFalse(UserContext.getUserId(), entityId);

			if (!mapped) {
				throw new BaseException(AuthErrorCode.AUTH_FORBIDDEN);
			}
		}

		return entityRepo.findById(entityId)
				.orElseThrow(() -> new ResourceNotFoundException(EntityErrorCode.ENTITY_NOT_FOUND, entityId));
	}

	// =====================================================
	// HELPERS
	// =====================================================

	private void updateNameIfNeeded(EntityMaster entity, EntityRequest request) {

		String newName = request.getEntityName();

		if (newName == null || newName.isBlank())
			return;

		if (!newName.equalsIgnoreCase(entity.getEntityName())) {

			validateNoDuplicateName(newName, entity.getEntityId());

			entity.setEntityName(newName.trim());
		}
	}

	private void updateIfPresent(Integer value, java.util.function.Consumer<Integer> setter) {

		if (value != null) {
			setter.accept(value);
		}
	}

	private void updateIfPresent(java.time.LocalDate value, java.util.function.Consumer<java.time.LocalDate> setter) {

		if (value != null) {
			setter.accept(value);
		}
	}

	private void updateIfPresent(String value, java.util.function.Consumer<String> setter) {

		if (value != null && !value.isBlank()) {
			setter.accept(value.trim());
		}
	}

	private void validateNoDuplicateName(String name, UUID excludeId) {

		boolean exists = (excludeId == null) ? entityRepo.existsByEntityNameIgnoreCaseAndIsDeletedFalse(name)
				: entityRepo.existsByEntityNameIgnoreCaseAndEntityIdNotAndIsDeletedFalse(name, excludeId);

		if (exists) {
			throw new ResourceNotFoundException(EntityErrorCode.ENTITY_ALREADY_EXISTS);
		}
	}

	private Pageable withDefaultSort(Pageable pageable) {

		if (pageable == null) {
			return PageRequest.of(0, 20, Sort.by("createdAt").descending());
		}

		if (pageable.getSort().isUnsorted()) {
			return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
		}

		return pageable;
	}

	@Override
	@Transactional
	public void mapUser(UUID entityId, EntityUserMappingRequest request) {

		requireRole(Role.ADMIN);

		EntityMaster entity = entityRepo.findByEntityIdAndIsDeletedFalse(entityId)

				.orElseThrow(() -> new ResourceNotFoundException(EntityErrorCode.ENTITY_NOT_FOUND, entityId));

		boolean alreadyMapped = mapperRepo.existsByEntityIdAndUserIdAndRoleIdAndIsDeletedFalse(entityId,
				request.getUserId(), request.getRoleId());
		if (alreadyMapped) {
			throw new BaseException(EntityErrorCode.ENTITY_ALREADT_MAPPED, "User already mapped to entity");
		}

		EntityUserMapper mapperEntity = EntityUserMapper.builder()

				.entityUserId(UUID.randomUUID()).entityId(entity.getEntityId()).userId(request.getUserId())
				.roleId(request.getRoleId()).ownershipPercentage(request.getOwnershipPercentage())
				.relationshipType(request.getRelationshipType()).investmentAmount(request.getInvestmentAmount())
				.build();
		mapperEntity.setVersion(0L);
		mapperEntity.setIsDeleted(false);
		mapperRepo.save(mapperEntity);
		mapperEntity.setVersion(0L);

		mapperEntity.setIsDeleted(false);

		log.info("REQUEST ROLE ID = {}", request.getRoleId());

		log.info("ENTITY ROLE ID = {}", mapperEntity.getRoleId());
		log.info("Mapped user={} to entity={} relationship={}", request.getUserId(), entityId,
				request.getRelationshipType());
	}

	@Override
	@Transactional
	public void mapUsers(UUID entityId, EntityBulkUserMappingRequest request) {

		requireRole(Role.ADMIN);

		if (request == null || request.getUsers() == null || request.getUsers().isEmpty()) {

			throw new BaseException(EntityErrorCode.ENTITY_RELATIONSHIP_TYPE, "Users list cannot be empty");
		}

		EntityMaster entity = entityRepo.findByEntityIdAndIsDeletedFalse(entityId)
				.orElseThrow(() -> new ResourceNotFoundException(EntityErrorCode.ENTITY_NOT_FOUND, entityId));

		for (EntityUserMappingRequest userRequest : request.getUsers()) {

			boolean alreadyInvested = mapperRepo.existsByEntityIdAndUserIdAndIsDeletedFalse(entityId,
					userRequest.getUserId());

			if (alreadyInvested) {

				throw new BaseException(EntityErrorCode.USER_ALREADY_INVESTED, "User already invested in this entity");
			}

			Set<String> allowedRelationships = Set.of("PRIMARY_INVESTOR", "REPRESENTATIVE");

			if (!allowedRelationships.contains(userRequest.getRelationshipType())) {

				log.warn("Skipping invalid relationship type for userId={}", userRequest.getUserId());

				continue;

			}

			EntityUserMapper mapperEntity = EntityUserMapper.builder()

					.entityUserId(UUID.randomUUID())

					.entityId(entity.getEntityId())

					.userId(userRequest.getUserId())

					.roleId(userRequest.getRoleId())

					.ownershipPercentage(userRequest.getOwnershipPercentage())

					.investmentAmount(userRequest.getInvestmentAmount())

					.relationshipType(userRequest.getRelationshipType())

					.build();

			mapperEntity.setVersion(0L);

			mapperEntity.setIsDeleted(false);

			mapperRepo.save(
			        mapperEntity
			);

			// =============================================
			// PRODUCE DOWNSTREAM EVENTS
			// =============================================

			}
		}
	}

	