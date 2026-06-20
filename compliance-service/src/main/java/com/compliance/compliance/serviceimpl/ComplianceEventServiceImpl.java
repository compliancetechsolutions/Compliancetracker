package com.compliance.compliance.serviceimpl;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.common.enums.ComplianceStatus;
import com.compliance.common.kafka.event.EntityEvent;
import com.compliance.compliance.dto.ComplianceRequest;
import com.compliance.compliance.repository.ComplianceRepository;
import com.compliance.compliance.service.ComplianceEventService;
import com.compliance.compliance.service.ComplianceService;
import com.compliance.enums.ComplianceFrequency;
import com.compliance.enums.ComplianceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceEventServiceImpl implements ComplianceEventService {

  private final ComplianceService complianceService;
  private final ComplianceRepository complianceRepository;

  @Override
  @Transactional
  public void processEntity(EntityEvent event) {

    if (event == null || event.getEntityId() == null) {
      return;
    }

    // =================================================
    // IDEMPOTENCY CHECK
    // Required because:
    // - EntityEventConsumer's AckMode is MANUAL with
    // auto.offset.reset=earliest, so consumer-group
    // resets / rebalances can redeliver ENTITY_CREATED
    // for entities already onboarded.
    // - The outbox relay (entity-service) guarantees
    // AT-LEAST-ONCE delivery, not exactly-once.
    // Without this check, every redelivery inserts another
    // ENTITY_ONBOARDING ComplianceRecord for the same entity.
    // =================================================

    boolean alreadyOnboarded = complianceRepository.existsByEntityIdAndComplianceType(event.getEntityId(),
        ComplianceType.ENTITY_ONBOARDING.name());

    if (alreadyOnboarded) {
      log.info("[COMPLIANCE] ENTITY_ONBOARDING already exists, skipping duplicate | entityId={}",
          event.getEntityId());
      return;
    }

    ComplianceRequest request = ComplianceRequest.builder().entityId(event.getEntityId())
        .activityId(UUID.randomUUID()).complianceType(ComplianceType.ENTITY_ONBOARDING.name())
        .frequency(ComplianceFrequency.ONE_TIME.name()).status(ComplianceStatus.PENDING_DELAYED.name())
        .dueDate(LocalDate.now().plusDays(30)).effectiveDate(LocalDate.now()).notificationRequired(true)
        .priority(5).remarks("Auto created from entity event").build();

    // ComplianceService.create() already publishes the Kafka event
    complianceService.create(request);

    log.info("[COMPLIANCE] created entity={}", event.getEntityId());
  }

  @Override
  public void updateEntity(EntityEvent event) {
    log.info("[COMPLIANCE] update entity={}", event.getEntityId());
  }
}