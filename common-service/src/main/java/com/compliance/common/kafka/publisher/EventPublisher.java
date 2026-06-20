package com.compliance.common.kafka.publisher;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.event.BaseEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventPublisher {

  private final KafkaTemplate<String, BaseEvent> kafkaTemplate;
  public void publish(String topic, BaseEvent event) {

    if (event.getId() == null) {
      event.setId(UUID.randomUUID());

    }
    kafkaTemplate.send(topic, event.getAggregateId().toString(), event);
  }

}