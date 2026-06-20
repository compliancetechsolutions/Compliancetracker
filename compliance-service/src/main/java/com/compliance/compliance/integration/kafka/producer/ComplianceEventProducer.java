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

// ==========================================
// GENERIC
// ==========================================

  public void publish(

      Object event

  ) {

    if (

    event instanceof ComplianceCreatedEvent

    ) {

      publishCreated(

          (ComplianceCreatedEvent)

          event

      );

    }

    else if (

    event instanceof ComplianceCompletedEvent

    ) {

      publishCompleted(

          (ComplianceCompletedEvent)

          event

      );

    }

    else {

      throw new IllegalArgumentException(

          "Unsupported compliance event"

      );

    }

  }

// ==========================================
// CREATED
// ==========================================

  public CompletableFuture<SendResult<String, Object>>

      publishCreated(

          ComplianceCreatedEvent event

  ) {

    return kafkaTemplate

        .send(

            createdTopic,

            event.getComplianceId().toString(),

            event

        )

        .whenComplete(

            (

                result,

                ex

            ) -> {

              if (

            ex

                ==

                null

            ) {

                log.info(

                    "Published compliance created {}",

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

// ==========================================
// COMPLETED
// ==========================================

public CompletableFuture<
        SendResult<
                String,
                Object
        >
>

publishCompleted(

        ComplianceCompletedEvent event

) {

    return kafkaTemplate

            .send(

                    completedTopic,

                    event
                            .getComplianceId()
                            .toString(),

                    event

            )

            .whenComplete(

                    (

                            result,

                            ex

                    ) -> {

                        if (

                                ex

                                ==

                                null

                        ) {

                            log.info(

                                    "Published compliance completed {}",

                                    event
                                            .getComplianceId()

                            );

                        }

                        else {

                            log.error(

                                    "Publish failed {}",

                                    event
                                            .getComplianceId(),

                                    ex

                            );

                        }

                    }

            );

}

}
