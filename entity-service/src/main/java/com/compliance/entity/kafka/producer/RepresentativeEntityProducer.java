package com.compliance.entity.kafka.producer;



import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.compliance.common.kafka.event.RepresentativeEntityEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepresentativeEntityProducer {

	// =====================================================
	// KAFKA TEMPLATE
	// =====================================================

	private final KafkaTemplate<String, RepresentativeEntityEvent> kafkaTemplate;

	// =====================================================
	// PUBLISH EVENT
	// =====================================================

	public void publish(RepresentativeEntityEvent event) {

		kafkaTemplate.send(

				"representative-entity-event",

				event.getEntityId().toString(),

				event

		).whenComplete((result, ex) -> {

			// =============================================
			// FAILURE
			// =============================================

			if (ex != null) {

				log.error("Failed to publish representative event", ex);
			}

			// =============================================
			// SUCCESS
			// =============================================

			else {

				log.info("Representative event published topic={} partition={} offset={}",
						result.getRecordMetadata().topic(), result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
			}
		});
	}
}