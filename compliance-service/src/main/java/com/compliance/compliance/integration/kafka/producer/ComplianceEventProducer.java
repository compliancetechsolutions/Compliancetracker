
package com.compliance.compliance.integration.kafka.producer;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.support.SendResult;

import org.springframework.stereotype.Component;

import com.compliance.compliance.event.ComplianceCompletedEvent;

import com.compliance.compliance.event.ComplianceCreatedEvent;

@Slf4j

@Component

@RequiredArgsConstructor

public class ComplianceEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${app.kafka.topics.compliance-created}")

	private String createdTopic;

	@Value("${app.kafka.topics.compliance-completed}")

	private String completedTopic;

// =====================================
// CREATED
// =====================================

	public void publishCreated(

			ComplianceCreatedEvent event

	) {

		CompletableFuture<SendResult<String, Object>>

		future =

				kafkaTemplate.send(

						createdTopic,

						event.getComplianceId().toString(),

						event

				);

		future.whenComplete(

				(result, ex) -> {

					if (

				ex == null

				) {

						log.info(

								"Published created event {}",

								event.getComplianceId()

				);

					}

				else {

						log.error(

								"Publish failed {}",

								event.getComplianceId(),

								ex

				);

					}

				}

		);

	}

// =====================================
// COMPLETED
// =====================================

	public void publishCompleted(

			ComplianceCompletedEvent event

	) {

		CompletableFuture<SendResult<String, Object>>

		future =

				kafkaTemplate.send(

						completedTopic,

						event.getComplianceId().toString(),

						event

				);

		future.whenComplete(

				(result, ex) -> {

					if (

				ex == null

				) {

						log.info(

								"Published completed {}",

								event.getComplianceId()

				);

					}

				else {

						log.error(

								"Publish completed failed {}",

								event.getComplianceId(),

								ex

				);

					}

				}

		);

	}

}
