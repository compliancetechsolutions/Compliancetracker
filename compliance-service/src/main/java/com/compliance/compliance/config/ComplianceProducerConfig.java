package com.compliance.compliance.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Kafka producer configuration optimised for high-throughput notification publishing.
 *
 * <p><b>Key settings for 2-billion-notification scale:</b>
 * <pre>
 *  acks=all                          — waits for all in-sync replicas to confirm
 *  enable.idempotence=true           — exactly-once delivery at the producer level
 *  linger.ms=5                       — batches records for 5 ms to amortise per-record overhead
 *  batch.size=65536 (64KB)           — larger batches = fewer broker round-trips
 *  compression.type=lz4              — fast compression; cuts bandwidth ~60%
 *  max.in.flight.requests.per.connection=5
 *                                    — max allowed with idempotence=true; enables pipelining
 *  buffer.memory=67108864 (64MB)     — in-memory send buffer; prevents back-pressure from flooding GC
 *  request.timeout.ms=30000          — allow slow brokers up to 30s before failing
 *  retries=Integer.MAX_VALUE         — retry indefinitely (idempotence prevents duplicates)
 *  delivery.timeout.ms=120000        — give up after 2 min total (broker unresponsive)
 * </pre>
 *
 * <p><b>FIX over original:</b>
 * <ul>
 *   <li>Original extended a generic {@code BaseKafkaProducerConfig<ComplianceCreatedEvent>}
 *       which only produced {@code ComplianceCreatedEvent}. The notification pipeline now
 *       publishes multiple event types (Created, Completed, NotificationEvent) — so this
 *       config exposes a generic {@code KafkaTemplate<String, Object>} that all producers share.</li>
 *   <li>Added idempotence, batching, compression, and buffer tuning — none were present.</li>
 * </ul>
 */
@Configuration
public class ComplianceProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // =====================================================
    // PRODUCER FACTORY
    // =====================================================

    @Bean
    public ProducerFactory<String, Object> complianceProducerFactory() {
        Map<String, Object> props = new HashMap<>();

        // ── connection ─────────────────────────────────────────────────────
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,      bootstrapServers);

        // ── serializers ────────────────────────────────────────────────────
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // ── reliability ────────────────────────────────────────────────────
        props.put(ProducerConfig.ACKS_CONFIG,                   "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,     true);
        props.put(ProducerConfig.RETRIES_CONFIG,                Integer.MAX_VALUE);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,    120_000);   // 2 min
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,     30_000);    // 30 s

        // ── throughput / batching ──────────────────────────────────────────
        props.put(ProducerConfig.LINGER_MS_CONFIG,              5);          // 5 ms batch window
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,             65_536);     // 64 KB
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,          67_108_864); // 64 MB
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // ── compression — lz4 is the best latency/throughput trade-off ────
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,       "lz4");

        return new DefaultKafkaProducerFactory<>(props);
    }

    // =====================================================
    // KAFKA TEMPLATE (generic Object value)
    // =====================================================

    /**
     * Shared {@link KafkaTemplate} used by all compliance producers.
     *
     * <p>Using a generic {@code Object} value type avoids creating separate
     * templates for each event class. The {@link JsonSerializer} handles
     * serialisation of any POJO at runtime.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(complianceProducerFactory());
    }
}
