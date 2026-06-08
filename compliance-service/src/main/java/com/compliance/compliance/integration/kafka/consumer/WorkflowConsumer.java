package com.compliance.compliance.integration.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.compliance.compliance.event.WorkflowTaskEvent;
import com.compliance.compliance.workflow.ComplianceWorkflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer for workflow task events.
 *
 * <p><b>FIX — NullPointerException on null check:</b>
 * Original called {@code log.info("Workflow event received {}", event.getWorkflowTaskId())}
 * BEFORE the null check on {@code event}. If Kafka delivers a null-body message
 * (e.g. tombstone record or deserialization failure fallback), this line throws NPE
 * before the guard can run. Fixed by moving the null check first.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowConsumer {

    private final ComplianceWorkflow workflow;

    @KafkaListener(
            topics           = "${app.kafka.topics.workflow-task}",
            groupId          = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(WorkflowTaskEvent event, Acknowledgment ack) {
        // FIX: null check BEFORE accessing event fields
        if (event == null || event.getWorkflowTaskId() == null) {
            log.warn("[WORKFLOW] Received null or invalid WorkflowTaskEvent — skipping");
            ack.acknowledge();
            return;
        }

        try {
            log.info("[WORKFLOW] Processing WorkflowTaskEvent | workflowTaskId={} workflowId={} complianceId={}",
                    event.getWorkflowTaskId(), event.getWorkflowId(), event.getComplianceId());

            workflow.execute(event);

            ack.acknowledge();

            log.info("[WORKFLOW] WorkflowTaskEvent completed | workflowTaskId={}", event.getWorkflowTaskId());

        } catch (Exception ex) {
            log.error("[WORKFLOW] WorkflowTaskEvent FAILED | workflowTaskId={} error={}",
                    event.getWorkflowTaskId(), ex.getMessage(), ex);
            throw ex; // rethrow → DLT after retries
        }
    }
}