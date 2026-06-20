package com.compliance.notification.kafka.dlt;

import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.event.NotificationEvent;
import com.compliance.enums.NotificationStatus;
import com.compliance.notification.entity.NotificationLog;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.kafka.publisher.ExhaustedNotificationPublisher;
import com.compliance.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDeadLetterConsumer {

  private final NotificationRepository notificationRepository;
  private final ExhaustedNotificationPublisher exhaustedPublisher;

  /**
   * Consumes from the DLT topic directly via @KafkaListener.
   * 
   * @DltHandler is NOT used — it only works inside a @KafkaListener class, not as
   *             a standalone bean.
   *
   *             On arrival: mark DEAD, increment retryCount. If retryCount >=
   *             MAX_DEAD_RETRIES: mark EXHAUSTED and publish to exhausted topic.
   */
  @KafkaListener(topics = "${app.kafka.topics.notification-dlt}", groupId = "${app.kafka.consumer.group-id}-dlt", containerFactory = "kafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, UUID> record, Acknowledgment ack) {
    UUID notificationId = record.value();
    log.warn("[DLT] Received dead-letter notificationId={} partition={} offset={}", notificationId,
        record.partition(), record.offset());

    try {
      notificationRepository.findById(notificationId).ifPresentOrElse(notification -> handleDead(notification),
          () -> log.error("[DLT] NotificationRecord not found for id={} — skipping", notificationId));
      ack.acknowledge();
    } catch (Exception ex) {
      log.error("[DLT] Failed to process dead-letter notificationId={}: {}", notificationId, ex.getMessage(), ex);
      // Do NOT nack — we don't want another DLT loop. Ack and alert via log/metric.
      ack.acknowledge();
    }
  }

  private static final int MAX_DEAD_RETRIES = 3;

  private void handleDead(NotificationRecord notification) {
    int newRetryCount = notification.getRetryCount() + 1;
    notification.setRetryCount(newRetryCount);

    if (newRetryCount >= MAX_DEAD_RETRIES) {
      log.error("[DLT] Exhausted after {} retries — notificationId={} channel={}", newRetryCount,
          notification.getId(), notification.getChannel());
      notification.setStatus(NotificationStatus.EXHAUSTED);
      notificationRepository.save(notification);
      NotificationLog log =

          NotificationLog.builder()

              .eventId(notification.getEventId())

              .entityId(notification.getAggregateId())

              .recipient(notification.getRecipient())

              .channel(notification.getChannel())

              .type(notification.getType())

              .status(NotificationStatus.EXHAUSTED)

              .message(notification.getMessage())

              .retryCount(notification.getRetryCount())

              .errorMessage("Notification exhausted after DLT retries")

              .build();

      NotificationEvent event =

          NotificationEvent.builder()

              .entityId(notification.getAggregateId())

              .userId(notification.getUserId())

              .notificationType(notification.getType().name())

              .title(notification.getTitle())

              .message(notification.getMessage())

              .retryCount(notification.getRetryCount())

              .build();

      exhaustedPublisher.publish(

          log,

          event

      );
    } else {
      log.warn("[DLT] Marking DEAD for retry — notificationId={} attempt={}/{}", notification.getId(),
          newRetryCount, MAX_DEAD_RETRIES);
      notification.setStatus(NotificationStatus.DEAD);
      notificationRepository.save(notification);
    }
  }
}
