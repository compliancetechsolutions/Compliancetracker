package com.compliance.auth.kafka.dlt;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.dlt.BaseDeadLetterConsumer;
import com.compliance.common.kafka.event.UserEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserEventDeadLetterConsumer

		extends BaseDeadLetterConsumer<UserEvent> {

	@KafkaListener(topics = "${app.kafka.topics.user-events-dlt}", groupId = "${spring.kafka.consumer.dlt-group-id}", containerFactory = "kafkaListenerContainerFactory")

	@DltHandler
	public void consume(

			UserEvent event

	) {

		if (event == null) {

			log.error("AUTH DLT EMPTY");

			return;

		}

		logDlt(

				"user-events.DLT",

				event

		);

	}

}
