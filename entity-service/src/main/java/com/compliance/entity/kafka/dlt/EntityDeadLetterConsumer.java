package com.compliance.entity.kafka.dlt;

import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.compliance.common.kafka.constants.KafkaTopics;
import com.compliance.common.kafka.event.FailedEvent;
import com.compliance.common.kafka.event.UserEvent;
import com.compliance.common.kafka.repository.FailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityDeadLetterConsumer {

	private final FailedEventRepository failedEventRepository;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = KafkaTopics.USER_EVENTS_DLT, groupId = "entity-dlt-group")
	public void consume(

			UserEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.OFFSET) long offset,
			@Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

		try {

			log.error(

					"DLT EVENT topic={} offset={} key={} eventId={}",

					topic,

					offset,

					key,

					event != null ? event.getEventId() : null);

			if (

			event == null

			) {

				return;
			}

			FailedEvent failed =

					FailedEvent.builder().id(event.getEventId()).aggregateId(event.getUserId())
							.eventType(event.getEventType()).serviceName("entity-service").topicName(topic)
							.payload(objectMapper.writeValueAsString(event)).status("DLT").retryCount(3)
							.timestamp(LocalDateTime.now()).createdAt(LocalDateTime.now())
							.updatedAt(LocalDateTime.now()).errorMessage("Moved to Dead Letter Topic").build();
			failedEventRepository.save(failed);

			log.info("DLT event persisted eventId={}", event.getEventId());

		}

		catch (

		Exception ex

		) {

			log.error(

					"DLT processing failed",

					ex);
		}

	}
}