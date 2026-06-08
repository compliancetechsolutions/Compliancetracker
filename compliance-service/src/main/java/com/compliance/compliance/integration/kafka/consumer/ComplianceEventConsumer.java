package com.compliance.compliance.integration.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.compliance.compliance.event.ComplianceCompletedEvent;
import com.compliance.compliance.event.ComplianceCreatedEvent;
import com.compliance.compliance.service.ComplianceService;
import com.compliance.compliance.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer for compliance lifecycle events.
 *
 * <p><b>FIXES over original:</b>
 * <ol>
 *   <li><b>Notification logic fixed in consumeCompleted()</b> — original only called
 *       {@code notificationService.sendAsync(plainTextMessage)} when
 *       {@code event.getNotificationSent() == true}, which is the WRONG flag.
 *       {@code notificationSent} is a flag set AFTER a notification is dispatched,
 *       not a request to dispatch one. Fixed to always call
 *       {@code notifyComplianceCompleted(event)} which builds the correct structured
 *       {@link com.compliance.compliance.event.NotificationEvent} with entity context.</li>
 *   <li><b>Structured logging</b> — original log messages had no field labels, making
 *       correlation across distributed traces difficult. All log lines now include
 *       named key=value pairs.</li>
 *   <li><b>Null-safe event body</b> — added guard before accessing event fields.</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplianceEventConsumer {

    private final ComplianceService   complianceService;
    private final NotificationService notificationService;

    // =====================================================
    // COMPLIANCE CREATED
    // =====================================================

    @KafkaListener(
            topics           = "${app.kafka.topics.compliance-created}",
            groupId          = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCreated(ComplianceCreatedEvent event, Acknowledgment ack) {
        if (event == null || event.getComplianceId() == null) {
            log.warn("[CONSUMER] Received null or invalid ComplianceCreatedEvent — skipping");
            ack.acknowledge();
            return;
        }

        try {
            UUID eventId = event.getEventId();

            if (eventId != null && complianceService.alreadyProcessed(eventId)) {
                log.info("[CONSUMER] Duplicate ComplianceCreatedEvent skipped | eventId={} complianceId={}",
                        eventId, event.getComplianceId());
                ack.acknowledge();
                return;
            }

            log.info("[CONSUMER] Processing ComplianceCreatedEvent | complianceId={} entityId={}",
                    event.getComplianceId(), event.getEntityId());

            complianceService.processCompliance(event);

            // Notify the entity that a new compliance obligation has been assigned
            notificationService.notifyComplianceCreated(event);

            ack.acknowledge();
            log.info("[CONSUMER] ComplianceCreatedEvent processed | complianceId={}", event.getComplianceId());

        } catch (Exception ex) {
            log.error("[CONSUMER] ComplianceCreatedEvent processing FAILED | complianceId={} error={}",
                    event.getComplianceId(), ex.getMessage(), ex);
            throw ex; // rethrow → DefaultErrorHandler retries → DLT after 3 attempts
        }
    }

    // =====================================================
    // COMPLIANCE COMPLETED
    // =====================================================

    @KafkaListener(
            topics           = "${app.kafka.topics.compliance-completed}",
            groupId          = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCompleted(ComplianceCompletedEvent event, Acknowledgment ack) {
        if (event == null || event.getComplianceId() == null) {
            log.warn("[CONSUMER] Received null or invalid ComplianceCompletedEvent — skipping");
            ack.acknowledge();
            return;
        }

        try {
            log.info("[CONSUMER] Processing ComplianceCompletedEvent | complianceId={} status={} compliant={}",
                    event.getComplianceId(), event.getStatus(), event.getCompliant());

            // 1. Persist the completion result
            complianceService.completeCompliance(event);

            // 2. Always dispatch a structured notification — the notificationSent flag
            //    is set AFTER notification, not used to decide WHETHER to notify.
            //    FIX: was checking event.getNotificationSent() which was always null/false.
            notificationService.notifyComplianceCompleted(event);

            ack.acknowledge();
            log.info("[CONSUMER] ComplianceCompletedEvent processed | complianceId={}", event.getComplianceId());

        } catch (Exception ex) {
            log.error("[CONSUMER] ComplianceCompletedEvent processing FAILED | complianceId={} error={}",
                    event.getComplianceId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}