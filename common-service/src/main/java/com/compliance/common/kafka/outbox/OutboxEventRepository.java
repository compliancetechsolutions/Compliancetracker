package com.compliance.common.kafka.outbox;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

  // =====================================================
  // BATCH FETCH FOR RELAY — SKIP LOCKED so multiple pods
  // don't grab and double-publish the same rows.
  // =====================================================
  @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "-2"))
  @Query(value = """
      SELECT * FROM event_schema.outbox_events
      WHERE status = 'PENDING'
      AND retry_count < :maxRetries
      ORDER BY created_at ASC
      LIMIT :batchSize
      FOR UPDATE SKIP LOCKED
      """, nativeQuery = true)
  List<OutboxEvent> lockBatchForRelay(@Param("batchSize") int batchSize, @Param("maxRetries") int maxRetries);

  @Modifying
  @Transactional
  @Query("""
      UPDATE OutboxEvent o
      SET o.status = 'SENT', o.sentAt = CURRENT_TIMESTAMP
      WHERE o.id = :id
      """)
  void markSent(@Param("id") UUID id);

  @Modifying
  @Transactional
  @Query("""
      UPDATE OutboxEvent o
      SET o.retryCount = o.retryCount + 1,
          o.errorMessage = :error,
          o.status = CASE WHEN o.retryCount + 1 >= :maxRetries THEN 'FAILED' ELSE 'PENDING' END
      WHERE o.id = :id
      """)
  void markFailed(@Param("id") UUID id, @Param("error") String error, @Param("maxRetries") int maxRetries);
}