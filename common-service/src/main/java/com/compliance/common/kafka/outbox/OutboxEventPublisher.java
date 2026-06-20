package com.compliance.common.kafka.outbox;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Records an outbound event in the same DB transaction as the business write. A
 * separate {@link OutboxRelayScheduler} polls and publishes to Kafka
 * asynchronously.
 *
 * This guarantees: event is recorded IFF the business transaction commits. No
 * dual-write race between DB and Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {

  private final OutboxEventRepository outboxRepository;
  private final ObjectMapper objectMapper;

  /**
   * MUST be called from within an existing @Transactional context (the same one
   * that persists the business entity). Propagation.MANDATORY enforces this —
   * fails fast if called outside a transaction, rather than silently committing
   * the outbox row independently of the business write.
   */
  @Transactional(propagation = Propagation.MANDATORY)
  public void save(String topicName, UUID aggregateId, String eventType, Object eventPayload) {

    try {
      String json = objectMapper.writeValueAsString(eventPayload);

      OutboxEvent outbox = OutboxEvent.builder().aggregateId(aggregateId).eventType(eventType)
          .topicName(topicName).messageKey(aggregateId.toString()).payload(json).status("PENDING")
          .retryCount(0).build();

      outboxRepository.save(outbox);

      log.debug("Outbox event recorded type={} aggregateId={} topic={}", eventType, aggregateId, topicName);

    } catch (Exception e) {
      // Serialization failure here SHOULD roll back the business transaction —
      // re-throw so @Transactional rollback applies to the caller too.
      throw new IllegalStateException("Failed to serialize outbox event payload", e);
    }
  }
}