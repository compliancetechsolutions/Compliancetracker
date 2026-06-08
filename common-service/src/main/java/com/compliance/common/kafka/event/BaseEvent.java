package com.compliance.common.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all Kafka domain events.
 *
 * <p><b>CRITICAL FIX — JPA annotations removed:</b>
 * The original had {@code @MappedSuperclass}, {@code @Id}, and {@code @Column}
 * from {@code jakarta.persistence} on this class. These annotations are for
 * JPA entities (database rows). A Kafka event is a plain POJO that is JSON-
 * serialised over the wire — it is never mapped to a database table directly.
 * Having {@code @MappedSuperclass} caused Spring to attempt registering this
 * as a JPA entity hierarchy, producing Hibernate bootstrap errors and confusion
 * in classpath scanning.
 *
 * <p><b>What is kept:</b>
 * <ul>
 *   <li>{@code @SuperBuilder} / {@code @NoArgsConstructor} / {@code @AllArgsConstructor}
 *       — required for Lombok builder inheritance in subclasses.</li>
 *   <li>{@code @JsonIgnoreProperties(ignoreUnknown = true)} — consumer tolerance:
 *       older producers may not send every field; we must not fail deserialization.</li>
 *   <li>{@code eventId} — used for idempotency deduplication in Redis.</li>
 *   <li>{@code timestamp} / {@code createdAt} — for observability and ordering.</li>
 * </ul>
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent {

    // =====================================================
    // IDENTITY
    // =====================================================

    /** Unique event ID — used as Redis deduplication key (TTL 24h). */
    private UUID eventId;

    /** Domain event type string, e.g. "COMPLIANCE_CREATED". */
    private String eventType;

    /** ID of the domain aggregate this event belongs to. */
    private UUID aggregateId;

    /** Name of the publishing micro-service. */
    private String serviceName;

    // =====================================================
    // RETRY / STATUS
    // =====================================================

    protected Integer retryCount;

    protected String status;

    // =====================================================
    // TIMESTAMPS
    // =====================================================

    /** When the business event occurred. */
    protected LocalDateTime timestamp;

    /** When this event object was created (may differ from timestamp). */
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;
}