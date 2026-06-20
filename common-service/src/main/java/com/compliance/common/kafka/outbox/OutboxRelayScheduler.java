package com.compliance.common.kafka.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Polls outbox_events for PENDING rows and publishes them to Kafka. Runs in
 * every service instance — SKIP LOCKED ensures no double-publish across pods.
 *
 * Each batch runs in its OWN short transaction (lockBatchForRelay / markSent /
 * markFailed are separately transactional), so a Kafka publish failure for one
 * row doesn't roll back the whole batch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

  private static final int BATCH_SIZE = 200;
  private static final int MAX_RETRIES = 5;

  private final OutboxEventRepository outboxRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate; // generic Object template
  private final ObjectMapper objectMapper;

  @Scheduled(fixedDelayString = "${outbox.relay.interval-ms:500}")
  @Transactional
  public void relay() {

    var batch = outboxRepository.lockBatchForRelay(BATCH_SIZE, MAX_RETRIES);

    if (batch.isEmpty()) {
      return;
    }

    for (OutboxEvent outbox : batch) {
      try {
        // payload was stored as JSON string; send as JsonNode so
        // ErrorHandlingDeserializer/JsonDeserializer on consumer side
        // can deserialize into the target event class via type headers
        // disabled (consumers use TRUSTED_PACKAGES + concrete type).
        JsonNode payloadNode = objectMapper.readTree(outbox.getPayload());

        kafkaTemplate.send(outbox.getTopicName(), outbox.getMessageKey(), payloadNode)
            .whenComplete((result, ex) -> {
              if (ex != null) {
                log.error("Outbox relay publish failed id={} topic={}", outbox.getId(),
                    outbox.getTopicName(), ex);
                outboxRepository.markFailed(outbox.getId(), ex.getMessage(), MAX_RETRIES);
              } else {
                outboxRepository.markSent(outbox.getId());
                log.debug("Outbox relay published id={} topic={} offset={}", outbox.getId(),
                    result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
              }
            })
            // block briefly so this row's status update happens
            // within the current relay() transaction
            .join();

      } catch (Exception e) {
        log.error("Outbox relay error id={}", outbox.getId(), e);
        outboxRepository.markFailed(outbox.getId(), e.getMessage(), MAX_RETRIES);
      }
    }
  }
}