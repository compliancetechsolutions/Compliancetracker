package com.compliance.entity.kafka.config;



import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.compliance.common.kafka.config.BaseKafkaConsumerConfig;
import com.compliance.common.kafka.event.UserEvent;


@Configuration
public class UserKafkaConsumerConfig
        extends BaseKafkaConsumerConfig<UserEvent> {

    public UserKafkaConsumerConfig() {

        super(UserEvent.class);
    }

    @Override
    protected String getGroupId() {

        return "entity-service-group";
    }

    @Bean
    public ConsumerFactory<String, UserEvent> consumerFactory() {

        Map<String, Object> config =
                buildConsumerConfig();

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(
                        UserEvent.class,
                        false
                )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent>
    userKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory()
        );

        return factory;
    }
}