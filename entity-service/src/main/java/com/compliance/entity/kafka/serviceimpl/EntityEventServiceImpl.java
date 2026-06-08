package com.compliance.entity.kafka.serviceimpl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.common.enums.EntityStatus;
import com.compliance.common.kafka.event.InvestorEntityEvent;
import com.compliance.common.kafka.event.RepresentativeEntityEvent;
import com.compliance.common.kafka.event.UserEvent;
import com.compliance.entity.entity.EntityMaster;
import com.compliance.entity.entity.EntityUserMapper;
import com.compliance.entity.kafka.producer.InvestorEntityProducer;
import com.compliance.entity.kafka.producer.RepresentativeEntityProducer;
import com.compliance.entity.kafka.service.EntityEventService;
import com.compliance.entity.repository.EntityRepository;
import com.compliance.entity.repository.EntityUserMapperRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EntityEventServiceImpl implements EntityEventService {

	// =====================================================
	// REPOSITORIES
	// =====================================================

	private final EntityRepository entityRepo;
	private final EntityUserMapperRepository mapperRepo;

	// =====================================================
	// PRODUCERS
	// =====================================================

	private final InvestorEntityProducer investorProducer;
	private final RepresentativeEntityProducer representativeProducer;

	// =====================================================
	// USER CREATED
	// =====================================================

	@Override
	public void handleUserCreated(UserEvent event) {

		log.info("Processing USER_CREATED userId={}", event.getUserId());

		// =================================================
		// VALIDATION
		// =================================================

		if (event == null || event.getUserId() == null) {

			log.warn("Invalid USER_CREATED event");

			return;
		}

		// =================================================
		// IDEMPOTENCY CHECK
		// =================================================

		boolean alreadyMapped = mapperRepo.countByUserIdAndIsDeletedFalse(event.getUserId()) > 0;

		if (alreadyMapped) {

			log.info("User already mapped userId={}", event.getUserId());

			return;
		}

		// =================================================
		// CREATE ENTITY
		// =================================================

		EntityMaster entity = EntityMaster.builder()

				.entityId(UUID.randomUUID())

				.entityName(event.getUsername() + "-ENTITY")

				.registrationNumber("AUTO-" + System.currentTimeMillis())

				.status(EntityStatus.ACTIVE)

				.build();

		entity.setVersion(0L);

		entity.setIsDeleted(false);

		EntityMaster savedEntity = entityRepo.save(entity);

		// =================================================
		// RELATIONSHIP TYPE
		// =================================================

		String relationshipType = event.getRoles() != null && event.getRoles().contains("INVESTOR")

				? "PRIMARY_INVESTOR"

				: "REPRESENTATIVE";

		// =================================================
		// MAP USER
		// =================================================

		EntityUserMapper mapperEntity = EntityUserMapper.builder()

				.entityUserId(UUID.randomUUID())

				.entityId(savedEntity.getEntityId())

				.userId(event.getUserId())

				.relationshipType(relationshipType)

				.build();

		mapperEntity.setVersion(0L);

		mapperEntity.setIsDeleted(false);

		mapperRepo.save(mapperEntity);

		// =================================================
		// PRODUCE INVESTOR EVENT
		// =================================================

		if ("PRIMARY_INVESTOR".equals(relationshipType)) {

			InvestorEntityEvent investorEvent = InvestorEntityEvent.builder()

					.eventId(UUID.randomUUID())

					.entityId(savedEntity.getEntityId())

					.userId(event.getUserId())

					.entityName(savedEntity.getEntityName())

					.relationshipType(relationshipType)

					.eventType("INVESTOR_ENTITY_CREATED")

					.build();

			investorProducer.publish(investorEvent);

			log.info("Investor event published entityId={}", savedEntity.getEntityId());
		}

		// =================================================
		// PRODUCE REPRESENTATIVE EVENT
		// =================================================

		else {

			RepresentativeEntityEvent representativeEvent = RepresentativeEntityEvent.builder()

					.eventId(UUID.randomUUID())

					.entityId(savedEntity.getEntityId())

					.userId(event.getUserId())

					.entityName(savedEntity.getEntityName())

					.relationshipType(relationshipType)

					.eventType("REPRESENTATIVE_ENTITY_CREATED")

					.build();

			representativeProducer.publish(representativeEvent);

			log.info("Representative event published entityId={}", savedEntity.getEntityId());
		}

		// =================================================
		// SUCCESS
		// =================================================

		log.info("Entity auto-created entityId={} userId={}", savedEntity.getEntityId(), event.getUserId());
	}

	// =====================================================
	// USER DELETED
	// =====================================================

	@Override
	public void handleUserDeleted(UserEvent event) {

		log.info("Processing USER_DELETED userId={}", event.getUserId());

		mapperRepo.findByUserIdAndIsDeletedFalse(event.getUserId()).forEach(mapper -> {

			mapper.setIsDeleted(true);

			mapperRepo.save(mapper);
		});

		log.info("User mappings soft deleted userId={}", event.getUserId());
	}

	// =====================================================
	// USER ROLE UPDATED
	// =====================================================

	@Override
	public void handleUserRoleUpdated(UserEvent event) {

		log.info("Processing USER_ROLE_UPDATED userId={} roles={}", event.getUserId(), event.getRoles());

		// =================================================
		// FUTURE ROLE SYNC LOGIC
		// =================================================
	}
}