package com.compliance.common.kafka.config;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.support.serializer.JsonDeserializer;

@RequiredArgsConstructor

public abstract class BaseKafkaConsumerConfig<T> {

  private final Class<T> eventClass;

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

// ==========================================
// OVERRIDABLE
// ==========================================

  protected abstract String getGroupId();

  protected int maxPollRecords() {

    return 500;

  }

  protected String offsetStrategy() {

    return "earliest";

  }

  protected int concurrency() {

    return 3;

  }

// ==========================================
// BUILD CONFIG
// ==========================================

  protected Map<String, Object> buildConsumerConfig() {

    Map<String, Object> config = new HashMap<>();

    config.put(

        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,

        bootstrapServers

    );

    config.put(

        ConsumerConfig.GROUP_ID_CONFIG,

        getGroupId()

    );

    config.put(

        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,

        StringDeserializer.class

    );

    config.put(

        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,

        JsonDeserializer.class

    );

    config.put(

        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,

        offsetStrategy()

    );

    config.put(

        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,

        false

    );

    config.put(

        ConsumerConfig.MAX_POLL_RECORDS_CONFIG,

        maxPollRecords()

    );

    config.put(

        ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,

        300000

    );

    config.put(

        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,

        30000

    );

    config.put(

        ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,

        10000

    );

    config.put(

        ConsumerConfig.FETCH_MIN_BYTES_CONFIG,

        1024

    );

    config.put(

        ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,

        500

    );

    config.put(

        JsonDeserializer.TRUSTED_PACKAGES,

        eventClass.getPackageName()

    );

    config.put(

        JsonDeserializer.VALUE_DEFAULT_TYPE,

        eventClass.getName()

    );

    config.put(

        JsonDeserializer.USE_TYPE_INFO_HEADERS,

        false

    );

    config.put(

        ConsumerConfig.ISOLATION_LEVEL_CONFIG,

        "read_committed"

    );

    return config;

  }

}
