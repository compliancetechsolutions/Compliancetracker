package com.compliance.compliance.integration.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.constants.KafkaTopics;
import com.compliance.common.kafka.event.EntityEvent;
import com.compliance.compliance.service.ComplianceEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityEventConsumer {

  private final ComplianceEventService service;

  @KafkaListener(topics = KafkaTopics.ENTITY_EVENTS, groupId = "compliance-group", containerFactory = "kafkaListenerContainerFactory")
  public void consume(EntityEvent event, Acknowledgment ack) {

    if (event == null || event.getEntityId() == null) {
      log.warn("[ENTITY-CONSUMER] Received null or invalid EntityEvent — skipping");
      ack.acknowledge();
      return;
    }

    try {
      log.info("[ENTITY-CONSUMER] Processing entityId={} eventType={}", event.getEntityId(),
          event.getEventType());

      switch (event.getEventType()) {

      case "ENTITY_CREATED":
        service.processEntity(event);
        break;

      case "ENTITY_UPDATED":
        service.updateEntity(event);
        break;

      default:
        log.warn("[ENTITY-CONSUMER] Unsupported eventType={} entityId={}", event.getEventType(),
            event.getEntityId());
      }

      ack.acknowledge();

      log.info("[ENTITY-CONSUMER] Completed entityId={} eventType={}", event.getEntityId(), event.getEventType());

    } catch (Exception ex) {
      log.error("[ENTITY-CONSUMER] Processing FAILED | entityId={} eventType={} error={}", event.getEntityId(),
          event.getEventType(), ex.getMessage(), ex);
      throw ex; // rethrow → DefaultErrorHandler retries → DLT after 3 attempts
    }
  }
}