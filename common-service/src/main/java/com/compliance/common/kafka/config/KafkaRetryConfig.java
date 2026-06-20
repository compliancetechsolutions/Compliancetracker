package com.compliance.common.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.listener.DefaultErrorHandler;

import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaRetryConfig {

  @Bean
  DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> template) {

    ExponentialBackOff backoff = new ExponentialBackOff();

    backoff.setInitialInterval(1000);

    backoff.setMultiplier(2);

    backoff.setMaxInterval(10000);

    return new DefaultErrorHandler(

        new DeadLetterPublishingRecoverer(template),

        backoff

    );

  }

}