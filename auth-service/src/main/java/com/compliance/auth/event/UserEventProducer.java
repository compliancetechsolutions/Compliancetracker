package com.compliance.auth.event;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.common.utils.CollectionUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.compliance.common.enums.KafkaErrorCode;
import com.compliance.common.enums.UserErrorCode;
import com.compliance.common.exception.BaseException;
import com.compliance.common.kafka.config.BaseKafkaProducerConfig;
import com.compliance.common.kafka.constants.KafkaTopics;
import com.compliance.common.kafka.event.FailedEvent;
import com.compliance.common.kafka.event.UserEvent;
import com.compliance.common.kafka.event.UserEventType;
import com.compliance.common.kafka.repository.FailedEventRepository;
import com.compliance.common.util.CorrelationIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserEventProducer extends BaseKafkaProducerConfig<UserEvent> {

	private final FailedEventRepository failedEventRepository;
	private final ObjectMapper objectMapper;

	public UserEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate,
			FailedEventRepository failedEventRepository, ObjectMapper objectMapper

	) {

		super(kafkaTemplate);
		this.failedEventRepository = failedEventRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	protected String getTopicName() {

		return KafkaTopics.USER_EVENTS;

	}

	public CompletableFuture<Void> publishUserCreatedEvent(

			UUID eventId,

			UserEvent event

	) {

		validate(event);

		initialize(

				eventId,

				event

		);

		return super.publish(

				event.getId().toString(),

				event

		);

	}

	public CompletableFuture<Void> publish(UserEvent event) {

		validate(event);

		initialize(UUID.randomUUID(), event);

		return super.publish(event.getId().toString(), event);

	}

	private void validate(UserEvent event) {

		if (event == null) {

			throw new IllegalArgumentException("UserEvent cannot be null");

		}

		if (event.getEventType() == UserEventType.CREATE_USER || event.getEventType() == UserEventType.UPDATE_USER) {

			if (event.getRoles() == null || event.getRoles().isEmpty()) {

				throw new BaseException(UserErrorCode.USER_MUST_HAVE_ROLE);

			}

		}

	}

	private void initialize(

			UUID eventId,

			UserEvent event

	) {

		if (event.getId() == null) {

			event.setId(eventId);

		}

		if (event.getAggregateId() == null) {

			event.setAggregateId(event.getUserId());

		}

		if (event.getTimestamp() == null) {

			event.setTimestamp(LocalDateTime.now());

		}

		if (event.getCreatedAt() == null) {

			event.setCreatedAt(LocalDateTime.now());

		}

		if (event.getEventType() == null) {

			event.setEventType("USER_CREATED");

		}

		if (event.getCorrelationId() == null) {

			event.setCorrelationId(

					CorrelationIdUtil.generate()

			);

		}

		event.setServiceName("auth-service");

		event.setStatus("NEW");

	}

	@Override
	protected void onPublishSuccess(

			String key,

			UserEvent event

	) {

		log.info(

				"Published user event type={} id={}",

				event.getEventType(),

				event.getId()

		);

	}

	@Override
	protected void onPublishFailure(

			String key,

			UserEvent event,

			Throwable ex

	) {

		log.error(

				KafkaErrorCode.KAFKA_PUBLISH_FAILED.getMessage(),

				ex

		);

		saveFailedEvent(event, ex);

	}

	private void saveFailedEvent(

			UserEvent event,

			Throwable ex

	) {

		try {

			StringWriter sw = new StringWriter();

			ex.printStackTrace(

					new PrintWriter(sw)

			);

			FailedEvent failed =

					FailedEvent.builder()

							.id(event.getId())

							.aggregateId(event.getAggregateId())
							.eventType(event.getEventType())
							.serviceName("auth-service")
							.retryCount(0)
							.status("FAILED")
							.timestamp(LocalDateTime.now())
							.topicName(getTopicName())
							.payload(objectMapper.writeValueAsString(event))
							.errorMessage(ex.getMessage())
							.stackTrace(sw.toString())
							.createdAt(LocalDateTime.now())
							.updatedAt(LocalDateTime.now())
							.build();
							failedEventRepository.save(failed);

		}

		catch (Exception e) {

			log.error(

					KafkaErrorCode.KAFKA_FALLBACK_SAVE_FAILED.getMessage(),

					e

			);

		}

	}

}
