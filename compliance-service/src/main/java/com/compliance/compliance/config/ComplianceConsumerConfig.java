package com.compliance.compliance.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka consumer configuration for the compliance service.
 *
 * <p><b>Fixes over original code:</b>
 * <ul>
 *   <li>Original extended {@code BaseKafkaConsumerConfig<ComplianceCreatedEvent>} — a generic
 *       base that can only be typed to one event class. The consumer now handles both
 *       {@link com.compliance.compliance.event.ComplianceCreatedEvent} and
 *       {@link com.compliance.compliance.event.ComplianceCompletedEvent}. Using a concrete
 *       config with explicit factory setup is cleaner and avoids type-erasure issues.</li>
 *   <li>Added {@link AckMode#MANUAL} — offsets committed only after successful processing,
 *       preventing data loss on pod crash. Original used default auto-commit.</li>
 *   <li>Added Dead-Letter Topic (DLT) publishing on repeated failure — original re-threw
 *       forever, blocking the partition.</li>
 *   <li>Added {@code concurrency} = number of partitions / service instances for parallelism.</li>
 *   <li>Added {@code ErrorHandlingDeserializer} so poison-pill messages route to DLT
 *       instead of crashing the listener thread.</li>
 * </ul>
 *
 * <p><b>Scale tuning:</b>
 * <pre>
 *   fetch.min.bytes=1024            — wait for 1 KB before returning a fetch response
 *   fetch.max.wait.ms=500           — max wait if fetch.min.bytes not met
 *   max.poll.records=500            — process up to 500 records per poll loop
 *   session.timeout.ms=45000        — consumer considered dead after 45s silence
 *   heartbeat.interval.ms=15000     — send heartbeat every 15s (must be < session.timeout/3)
 * </pre>
 */
@EnableKafka
@Configuration
public class ComplianceConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:compliance-group}")
    private String groupId;

    /** Number of concurrent listener threads per container — should match topic partition count. */
    @Value("${spring.kafka.listener.concurrency:10}")
    private int concurrency;

    // =====================================================
    // CONSUMER FACTORY
    // =====================================================

    @Bean
    public ConsumerFactory<String, Object> complianceConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // ── connection ─────────────────────────────────────────────────────
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,           groupId);

        // ── serializers ────────────────────────────────────────────────────
        // ErrorHandlingDeserializer wraps JsonDeserializer so that a single
        // malformed record (poison pill) is routed to the DLT instead of
        // crashing the entire partition listener.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JsonDeserializer.class);

        // Allow any package from our domain
        props.put(JsonDeserializer.TRUSTED_PACKAGES,        "com.compliance.*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,   false);

        // ── commit strategy — manual; see AckMode below ───────────────────
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // ── throughput / fetch tuning ──────────────────────────────────────
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,    1024);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,  500);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,   500);

        // ── session / heartbeat ────────────────────────────────────────────
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,   45_000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 15_000);

        // ── offset reset — earliest so no event is missed on new group ────
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // =====================================================
    // LISTENER CONTAINER FACTORY
    // =====================================================

    /**
     * Primary factory referenced by all {@code @KafkaListener} annotations via
     * {@code containerFactory = "kafkaListenerContainerFactory"}.
     *
     * @param kafkaTemplate used by the {@link DeadLetterPublishingRecoverer} to publish
     *                      failed records to {@code <topic>.DLT}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
            kafkaListenerContainerFactory(KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(complianceConsumerFactory());

        // ── manual ACK ────────────────────────────────────────────────────
        factory.getContainerProperties().setAckMode(AckMode.MANUAL);

        // ── concurrency ────────────────────────────────────────────────────
        factory.setConcurrency(concurrency);

        // ── error handler: 3 retries with 1s gap, then dead-letter ────────
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate);

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3));

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
