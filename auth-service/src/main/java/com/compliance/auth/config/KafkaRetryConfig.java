
package com.compliance.auth.config;

import org.apache.kafka.common.TopicPartition;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.listener.DefaultErrorHandler;

import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaRetryConfig {

  @Bean
  public DefaultErrorHandler kafkaErrorHandler(

      KafkaTemplate<Object, Object> kafkaTemplate

  ) {

    DeadLetterPublishingRecoverer recoverer =

        new DeadLetterPublishingRecoverer(

            kafkaTemplate,

            (

                record,

                exception

            ) ->

            new TopicPartition(

                record.topic()

                    +

                    ".DLT",

                record.partition()

            )

        );

    DefaultErrorHandler handler =

        new DefaultErrorHandler(

            recoverer,

            new FixedBackOff(

                3000,

                3

            )

        );

    handler.setAckAfterHandle(false);

    return handler;

  }

}
