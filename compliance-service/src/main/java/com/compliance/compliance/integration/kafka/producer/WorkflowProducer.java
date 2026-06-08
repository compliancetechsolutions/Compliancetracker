package com.compliance.compliance.integration.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.compliance.compliance.event.WorkflowTaskEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka producer for workflow task events.
 *
 * <p><b>FIX:</b> Original hardcoded topic name {@code "workflow.task"} as a
 * string literal. Changed to {@code @Value}-injected property so it is
 * consistent with the consumer and configurable per environment.
 * Also added partition key ({@code complianceId}) for ordering guarantees.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.workflow-task:workflow.task}")
    private String workflowTopic;

    public void publish(WorkflowTaskEvent event) {
        if (event == null || event.getWorkflowTaskId() == null) {
            log.warn("[WORKFLOW-PRODUCER] Called with null event — skipping");
            return;
        }

        // Partition by complianceId so all workflow tasks for one compliance
        // land in the same partition (preserves ordering per compliance)
        String partitionKey = event.getComplianceId() != null
                ? event.getComplianceId().toString()
                : event.getWorkflowTaskId().toString();

        kafkaTemplate.send(workflowTopic, partitionKey, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("[WORKFLOW-PRODUCER] Published | workflowTaskId={} complianceId={} partition={} offset={}",
                                event.getWorkflowTaskId(), event.getComplianceId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("[WORKFLOW-PRODUCER] Publish FAILED | workflowTaskId={} error={}",
                                event.getWorkflowTaskId(), ex.getMessage(), ex);
                    }
                });
    }
}