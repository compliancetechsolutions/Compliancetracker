package com.compliance.notification.kafka.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.event.NotificationEvent;
import com.compliance.notification.entity.NotificationLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Publishes a record to {@code notification-events.exhausted} when a
 * notification has failed {@code MAX_RETRIES} times and will not be retried
 * further. Ops/alerting can consume this topic to page on-call or surface in
 * a dashboard, without polling the {@code notification_logs} table.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExhaustedNotificationPublisher {

    @org.springframework.beans.factory.annotation.Value(
            "${app.kafka.topics.notification-exhausted:notification-events.exhausted}")
    private String exhaustedTopic;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void publish(NotificationLog log_, NotificationEvent event) {
        try {
            kafkaTemplate.send(exhaustedTopic,
                    log_.getEntityId() != null ? log_.getEntityId().toString() : log_.getId().toString(),
                    log_);
        } catch (Exception e) {
            log.error("[EXHAUSTED-PUBLISHER] Failed to publish exhausted notification logId={}",
                    log_.getId(), e);
        }
    }
}