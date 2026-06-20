package com.compliance.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Customizes the auto-configured Kafka listener container factory to add DLT
 * routing on failure.
 *
 * <p>
 * Everything else (bootstrap servers, group-id, ack-mode=manual_immediate,
 * deserializers, concurrency, etc.) comes from {@code spring.kafka.*} in
 * application.yml via Spring Boot's {@code KafkaAutoConfiguration} —
 * {@link ConsumerFactory} and {@link KafkaTemplate} beans (declared as
 * {@code <Object, Object>} by auto-config) are provided automatically and
 * injected here.
 *
 * <p>
 * Defining a {@code @Bean} named {@code kafkaListenerContainerFactory}
 * overrides the auto-configured one — this is the standard Spring Boot pattern
 * for customizing Kafka listener containers while keeping property-based
 * configuration for everything else.
 *
 * <p>
 * After 3 failed attempts (1s fixed backoff), the record is published to
 * {@code <topic>.DLT} (e.g. {@code notification-events.DLT}, matching
 * {@code app.kafka.topics.notification-dlt}), where
 * {@link com.compliance.notification.kafka.dlt.NotificationDltConsumer}
 * persists it as a {@code FailedEvent}.
 */
@Configuration
public class NotificationKafkaErrorHandlingConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
      ConsumerFactory<Object, Object> consumerFactory, KafkaTemplate<Object, Object> kafkaTemplate) {

    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3));
    factory.setCommonErrorHandler(errorHandler);

    return factory;
  }
}