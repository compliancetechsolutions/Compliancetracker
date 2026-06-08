package com.compliance.auth.event;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.compliance.common.enums.KafkaErrorCode;

import com.compliance.common.kafka.config.BaseKafkaProducerConfig;

import com.compliance.common.kafka.event.FailedEvent;
import com.compliance.common.kafka.event.UserEvent;

import com.compliance.common.kafka.repository.FailedEventRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserEventProducer
        extends BaseKafkaProducerConfig<
                UserEvent
                > {

    // =====================================================
    // REPOSITORIES
    // =====================================================

    private final FailedEventRepository
            failedEventRepository;

    private final ObjectMapper
            objectMapper;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserEventProducer(

            KafkaTemplate<
                    String,
                    UserEvent
                    > kafkaTemplate,

            FailedEventRepository
                    failedEventRepository,

            ObjectMapper
                    objectMapper
    ) {

        super(kafkaTemplate);

        this.failedEventRepository =
                failedEventRepository;

        this.objectMapper =
                objectMapper;
    }

    // =====================================================
    // TOPIC NAME
    // =====================================================

    @Override
    protected String getTopicName() {

        return "user-events";
    }

    // =====================================================
    // USER CREATED EVENT
    // =====================================================

    public void publishUserCreatedEvent(

            UUID eventId,

            UserEvent event
    ) {

        // =====================================================
        // VALIDATE EVENT
        // =====================================================

        if (event == null) {

            throw new IllegalArgumentException(
                    "UserEvent cannot be null"
            );
        }

        // =====================================================
        // SET EVENT ID IF NULL
        // =====================================================

        if (event.getEventId() == null) {

            event.setEventId(

                    eventId != null
                            ? eventId
                            : UUID.randomUUID()
            );
        }

        // =====================================================
        // SET TIMESTAMP IF NULL
        // =====================================================

        if (event.getTimestamp() == null) {

            event.setTimestamp(
                    LocalDateTime.now()
            );
        }

        // =====================================================
        // SET DEFAULT EVENT TYPE
        // =====================================================

        if (event.getEventType() == null) {

            event.setEventType(
                    "USER_CREATED"
            );
        }

        // =====================================================
        // PUBLISH EVENT
        // =====================================================

        publish(event);
    }

    // =====================================================
    // GENERIC PUBLISH
    // =====================================================

    public void publish(
            UserEvent event
            
    ) 
    
    
    {

        super.publish(

                event.getEventId().toString(),

                event
        );
    }

    // =====================================================
    // SUCCESS CALLBACK
    // =====================================================

    @Override
    protected void onPublishSuccess(

            String key,

            UserEvent event
    ) {

        log.info(
                "User event published successfully eventType={} eventId={}",
                event.getEventType(),
                event.getEventId()
        );
    }

    // =====================================================
    // FAILURE CALLBACK
    // =====================================================

    @Override
    protected void onPublishFailure(

            String key,

            UserEvent event,

            Throwable ex
    ) {

        log.error(
                KafkaErrorCode
                        .KAFKA_PUBLISH_FAILED
                        .getMessage(),
                ex
        );

        fallbackSave(
                event,
                ex
        );
    }

    // =====================================================
    // FALLBACK SAVE
    // =====================================================

    private void fallbackSave(

            UserEvent event,

            Throwable ex
    ) {

        try {

            FailedEvent failedEvent =
                    FailedEvent.builder()

                            // =========================
                            // BASE EVENT
                            // =========================

                            .eventId(
                                    event.getEventId()
                            )

                            .eventType(
                                    event.getEventType()
                            )

                            .serviceName(
                                    "auth-service"
                            )

                            .timestamp(
                                    LocalDateTime.now()
                            )

                            .retryCount(
                                    0
                            )

                            .status(
                                    "FAILED"
                            )

                            // =========================
                            // FAILED EVENT
                            // =========================

                            .topicName(
                                    getTopicName()
                            )

                            .payload(
                                    objectMapper
                                            .writeValueAsString(
                                                    event
                                            )
                            )

                            .errorMessage(
                                    ex.getMessage()
                            )

                            .stackTrace(
                                    ex.toString()
                            )

                            .createdAt(
                                    LocalDateTime.now()
                            )

                            .updatedAt(
                                    LocalDateTime.now()
                            )

                            .build();

            failedEventRepository.save(
                    failedEvent
            );

            log.info(
                    "Failed event persisted eventId={}",
                    event.getEventId()
            );

        } catch (Exception e) {

            log.error(
                    KafkaErrorCode
                            .KAFKA_FALLBACK_SAVE_FAILED
                            .getMessage(),
                    e
            );
        }
    }
}