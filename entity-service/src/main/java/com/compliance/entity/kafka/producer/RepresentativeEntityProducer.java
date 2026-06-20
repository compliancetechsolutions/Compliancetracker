package com.compliance.entity.kafka.producer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.compliance.common.enums.KafkaErrorCode;
import com.compliance.common.kafka.config.BaseKafkaProducerConfig;
import com.compliance.common.kafka.constants.KafkaTopics;
import com.compliance.common.kafka.event.FailedEvent;
import com.compliance.common.kafka.event.RepresentativeEntityEvent;
import com.compliance.common.kafka.repository.FailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RepresentativeEntityProducer extends BaseKafkaProducerConfig<RepresentativeEntityEvent> {

	private final FailedEventRepository failedEventRepository;
	private final ObjectMapper objectMapper;

	public RepresentativeEntityProducer(KafkaTemplate<String, RepresentativeEntityEvent> kafkaTemplate,
			FailedEventRepository failedEventRepository, ObjectMapper objectMapper) {

		super(kafkaTemplate);

		this.failedEventRepository = failedEventRepository;

		this.objectMapper = objectMapper;
	}

	// ===========================
	// PUBLISH
	// ===========================

	public void publish(RepresentativeEntityEvent event) {

		super.publish(event.getEntityId().toString(), event);
	}

	@Override
	protected String getTopicName() {

		return KafkaTopics.REPRESENTATIVE_ENTITY_EVENTS;
	}

	@Override
	protected void onPublishSuccess(String key, RepresentativeEntityEvent event) {

		log.info("Representative event published entityId={} eventType={}", event.getEntityId(), event.getEventType());
	}

	@Override
	protected void onPublishFailure(String key, RepresentativeEntityEvent event, Throwable ex) {

		log.error(KafkaErrorCode.KAFKA_PUBLISH_FAILED.getMessage(), ex);

		try {

			StringWriter sw = new StringWriter();

			ex.printStackTrace(new PrintWriter(sw));

			FailedEvent failed = FailedEvent.builder().id(event.getEventId()).aggregateId(event.getEntityId())
					.eventType(event.getEventType()).serviceName("entity-service").retryCount(0).status("FAILED")
					.timestamp(LocalDateTime.now()).topicName(getTopicName())
					.payload(objectMapper.writeValueAsString(event)).errorMessage(ex.getMessage())
					.stackTrace(sw.toString()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

			failedEventRepository.save(failed);

		} catch (Exception e) {

			log.error(KafkaErrorCode.KAFKA_FALLBACK_SAVE_FAILED.getMessage(), e);
		}
	}
}