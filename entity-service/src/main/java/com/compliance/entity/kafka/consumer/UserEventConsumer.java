package com.compliance.entity.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.messaging.handler.annotation.Header;

import org.springframework.stereotype.Service;

import com.compliance.common.enums.KafkaErrorCode;

import com.compliance.common.kafka.constants.KafkaTopics;

import com.compliance.common.kafka.event.UserEvent;

import com.compliance.entity.kafka.service.EntityEventService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

	private final EntityEventService entityEventService;

	@KafkaListener(topics = KafkaTopics.USER_EVENTS,

			groupId = "entity-service-group",

			containerFactory = "userKafkaListenerContainerFactory")
	public void consumeUserEvent(

			UserEvent event,

			Acknowledgment ack,

			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,

			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,

			@Header(KafkaHeaders.OFFSET) long offset

	) {

		try {

			if (event == null) {

				ack.acknowledge();

				return;
			}

			log.info(

					"Kafka consumed topic={} partition={} offset={} type={}",

					topic,

					partition,

					offset,

					event.getEventType());

			switch (

			event.getEventType()

			) {

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

				log.warn("Unknown event type {}", event.getEventType());
			}

			// COMMIT OFFSET ONLY AFTER SUCCESS
			ack.acknowledge();

		}

		catch (

		Exception ex

		) {

			log.error(

					KafkaErrorCode.KAFKA_CONSUMER_FAILED.getMessage(),

					ex);

			// no ack -> retry -> DLT
			throw ex;
		}
	}
}