package com.compliance.auth.config;

import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.JsonSerializer;

import com.compliance.common.kafka.event.UserEvent;

@Configuration
public class KafkaProducerConfig {

    // =====================================================
    // PRODUCER FACTORY
    // =====================================================

    @Bean
    public ProducerFactory<
            String,
            UserEvent
            > producerFactory() {

        Map<String, Object> config =
                Map.of(

                        "bootstrap.servers",
                        "kafka:9092",

                        "key.serializer",
                        StringSerializer.class,

                        "value.serializer",
                        JsonSerializer.class,

                        "acks",
                        "all",

                        "retries",
                        10,

                        "enable.idempotence",
                        true,

                        "compression.type",
                        "gzip"
                );

        return new DefaultKafkaProducerFactory<>(
                config
        );
    }

    // =====================================================
    // KAFKA TEMPLATE
    // =====================================================

    @Bean
    public KafkaTemplate<
            String,
            UserEvent
            > kafkaTemplate() {

        return new KafkaTemplate<>(
                producerFactory()
        );
    }
}