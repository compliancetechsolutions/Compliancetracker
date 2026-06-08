package com.compliance.entity.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.compliance.common.enums.KafkaErrorCode;
import com.compliance.common.kafka.event.UserEvent;
import com.compliance.entity.kafka.service.EntityEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

	// =====================================================
	// EVENT SERVICE
	// =====================================================

	private final EntityEventService entityEventService;

	// =====================================================
	// CONSUME USER EVENTS
	// =====================================================

	@KafkaListener(topics = "user-events", groupId = "entity-service-group", containerFactory = "userKafkaListenerContainerFactory")
	public void consumeUserEvent(

			UserEvent event,

			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,

			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,

			@Header(KafkaHeaders.OFFSET) long offset) {

		try {

			log.info("Kafka message consumed topic={} partition={} offset={} eventType={}", topic, partition, offset,
					event.getEventType());

			// =================================================
			// EVENT ROUTING
			// =================================================

			switch (event.getEventType()) {

			case "USER_CREATED":

				entityEventService.handleUserCreated(event);

				break;

			case "USER_DELETED":

				entityEventService.handleUserDeleted(event);

				break;

			case "USER_ROLE_UPDATED":

				entityEventService.handleUserRoleUpdated(event);

				break;

			default:

				log.warn("Unknown event type : {}", event.getEventType());
			}

		} catch (Exception ex) {

			log.error(KafkaErrorCode.KAFKA_CONSUMER_FAILED.getMessage(), ex);

			throw ex;
		}
	}
}