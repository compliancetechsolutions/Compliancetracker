package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KafkaErrorCode implements BaseErrorCode {

    // =========================================
    // PRODUCER ERRORS
    // =========================================

    KAFKA_PUBLISH_FAILED(
            "KAFKA_001",
            "Kafka message publish failed",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    KAFKA_FALLBACK_SAVE_FAILED(
            "KAFKA_002",
            "Kafka fallback persistence failed",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    
    KAFKA_MESSAGE_PRODUCED("KAFKA_008","Message Produced successfully",HttpStatus.CREATED),
    
    

    // =========================================
    // CONSUMER ERRORS
    // =========================================

    KAFKA_CONSUMER_FAILED(
            "KAFKA_003",
            "Kafka consumer processing failed",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    
    KAFKA_MESSAGE_CONSUMED("KAFKA_004","Message consumed successfully",HttpStatus.CREATED),

    KAFKA_POISON_MESSAGE(
            "KAFKA_004",
            "Poison message detected",
            HttpStatus.BAD_REQUEST
    ),

    KAFKA_DLT_PROCESSING_FAILED(
            "KAFKA_005",
            "DLT processing failed",
            HttpStatus.INTERNAL_SERVER_ERROR
    );
    private final String code;
    private final String message;
    private final HttpStatus status;
}