package com.compliance.common.kafka.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 * Base class for all Kafka domain events.
 *
 * Shared across: * auth-service * entity-service * compliance-service *
 * notification-service * investor-service * initiator-service
 *
 * NOTE: This is NOT a JPA entity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
public abstract class BaseEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  // =====================================================
  // IDENTITY
  // =====================================================

  /**
   * 
   * Unique event ID.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * 
   * Example: USER_CREATED ENTITY_CREATED
   */
  private String eventType;

  /**
   * 
   * Aggregate identifier.
   */
  private UUID aggregateId;

  /**
   * 
   * Publishing service.
   */
  private String serviceName;

  /**
   * 
   * Event schema version.
   */
  private Integer version = 1;

  /**
   * 
   * Distributed tracing.
   */
  private String correlationId;

  // =====================================================
  // RETRY / STATUS
  // =====================================================

  protected Integer retryCount = 0;

  protected String status = "NEW";

  // =====================================================
  // TIMESTAMPS
  // =====================================================

  /**
   * 
   * Business event timestamp.
   */
  protected LocalDateTime timestamp = LocalDateTime.now();

  /**
   * 
   * Event creation timestamp.
   */
  protected LocalDateTime createdAt = LocalDateTime.now();

  /**
   * 
   * Last modification timestamp.
   */
  protected LocalDateTime updatedAt;

  public UUID getEventId() {

    return id;

  }

  public void setEventId(

      UUID eventId

  ) {

    this.id = eventId;

  }

}
