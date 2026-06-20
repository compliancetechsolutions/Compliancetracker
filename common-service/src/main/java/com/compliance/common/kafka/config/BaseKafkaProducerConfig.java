package com.compliance.common.kafka.config;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseKafkaProducerConfig<T> {

  // =====================================================
  // KAFKA TEMPLATE
  // =====================================================

  protected final KafkaTemplate<String, T> kafkaTemplate;

  // =====================================================
  // EXPLICIT CONSTRUCTOR (not relying on Lombok
  // @RequiredArgsConstructor, which can fail to generate
  // on abstract generic classes depending on Lombok/JDK
  // version mismatches)
  // =====================================================

  protected BaseKafkaProducerConfig(KafkaTemplate<String, T> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  // =====================================================
  // TOPIC NAME
  // =====================================================

  protected abstract String getTopicName();

  // =====================================================
  // PUBLISH EVENT
  // =====================================================

  public CompletableFuture<Void> publish(String key, T event) {

    if (event == null) {

      throw new IllegalArgumentException("event cannot be null");

    }

    long start = System.currentTimeMillis();

    return kafkaTemplate

        .send(getTopicName(), key, event)

        .whenComplete(

            (result, ex) -> {

              if (ex != null) {

                log.error(

                    "Kafka publish failed topic={} key={}",

                    getTopicName(),

                    key,

                    ex

            );

                onPublishFailure(

                    key,

                    event,

                    ex

            );

              }

            else {

                long duration =

                    System.currentTimeMillis()

                        -

                        start;

                log.info(

                    "Published topic={} partition={} offset={} duration={}ms",

                    result.getRecordMetadata().topic(),

                    result.getRecordMetadata().partition(),

                    result.getRecordMetadata().offset(),

                    duration

            );

                onPublishSuccess(

                    key,

                    event

            );

              }

            }

        )

        .thenAccept(r -> {
        });

  }
  // =====================================================
  // SUCCESS CALLBACK
  // =====================================================

  protected void onPublishSuccess(String key, T event) {

    // optional override
  }

  // =====================================================
  // FAILURE CALLBACK
  // =====================================================

  protected void onPublishFailure(String key, T event, Throwable ex) {

    // optional override
  }
}