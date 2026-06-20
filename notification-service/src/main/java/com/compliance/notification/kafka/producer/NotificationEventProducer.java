package com.compliance.notification.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.config.BaseKafkaProducerConfig;
import com.compliance.common.kafka.constants.KafkaTopics;
import com.compliance.common.kafka.event.NotificationRequestedEvent;

@Component
public class NotificationEventProducer

    extends BaseKafkaProducerConfig<NotificationRequestedEvent> {

// =====================================================
// CONSTRUCTOR
// =====================================================

  public NotificationEventProducer(

      KafkaTemplate<String, NotificationRequestedEvent>

      kafkaTemplate

  ) {

    super(

        kafkaTemplate

    );

  }

  @Override
  protected String getTopicName() {

    return KafkaTopics.NOTIFICATION_EVENTS;

  }

}