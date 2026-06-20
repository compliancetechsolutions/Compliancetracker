package com.compliance.notification.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.support.Acknowledgment;

import org.springframework.stereotype.Component;

import com.compliance.common.kafka.config.BaseKafkaConsumerConfig;

import com.compliance.common.kafka.event.ComplianceCreatedEvent;

import com.compliance.notification.dto.NotificationRequest;

import com.compliance.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ComplianceCreatedConsumer

    extends BaseKafkaConsumerConfig<ComplianceCreatedEvent> {

  private final NotificationService notificationService;

  public ComplianceCreatedConsumer(

      NotificationService notificationService

  ) {

    super(

        ComplianceCreatedEvent.class

    );

    this.notificationService =

        notificationService;

  }

  @Override
  protected String getGroupId() {

    return

    "notification-group";

  }

  @KafkaListener(

      topics = "${app.kafka.topics.compliance-created}",

      groupId = "notification-group",

      containerFactory = "kafkaListenerContainerFactory"

  )

  public void consume(

      ComplianceCreatedEvent event,

      Acknowledgment ack

  ) {

    try {

      NotificationRequest request =

          NotificationRequest.builder()

              .eventId(event.getId())

              .aggregateId(event.getAggregateId())

              .userId(null)

              .recipient(resolveRecipient())

              .title("Compliance Created")

              .payload(buildMessage(event))

              .build();

      UUID id =

          notificationService.create(

              request

          );

      ack.acknowledge();

      log.info(

          "Notification created {}",

          id

      );

    }

    catch (

    Exception ex

    ) {

      log.error(

          "Notification consume failed",

          ex

      );

      throw ex;

    }

  }

// ====================================
// MESSAGE
// ====================================

  private String buildMessage(

      ComplianceCreatedEvent event

  ) {

    return

    "Compliance created successfully";

  }

// ====================================
// RECIPIENT
// ====================================

  private String resolveRecipient() {

    return

    "[admin@company.com](mailto:admin@company.com)";

  }

}
