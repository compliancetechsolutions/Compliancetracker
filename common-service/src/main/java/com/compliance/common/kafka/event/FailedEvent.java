package com.compliance.common.kafka.event;

import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "failed_events", schema = "event_schema")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FailedEvent extends BaseEvent {

   // =====================================================
    // TOPIC NAME
    // =====================================================

    @Column(name = "topic_name")
    private String topicName;

    // =====================================================
    // PAYLOAD
    // =====================================================

    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    // =====================================================
    // ERROR MESSAGE
    // =====================================================

    @Column(name = "error_message")
    private String errorMessage;

    // =====================================================
    // STACK TRACE
    // =====================================================

    @Column(name = "stack_trace")
    private String stackTrace;

   
    // =====================================================
    // JPA CALLBACKS
    // =====================================================

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        this.updatedAt = LocalDateTime.now();

        if (this.timestamp == null) {

            this.timestamp = LocalDateTime.now();
        }

        if (this.retryCount == null) {

            this.retryCount = 0;
        }

        if (this.status == null) {

            this.status = "FAILED";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}