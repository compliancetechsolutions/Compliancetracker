package com.compliance.common.kafka.repository;

import com.compliance.common.kafka.event.FailedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface FailedEventRepository
        extends BaseEventRepository<FailedEvent> {

    // =====================================================
    // FIND BY EVENT ID
    // =====================================================

    Optional<FailedEvent> findByEventId(
            UUID eventId
    );

    // =====================================================
    // FAILED EVENTS FOR RETRY
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.status = 'FAILED'
            AND f.retryCount < :retryCount
            ORDER BY f.createdAt ASC
            """)
    List<FailedEvent> findRetryableEvents(
            @Param("retryCount")
            Integer retryCount
    );

    // =====================================================
    // FIND BY TOPIC
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.topicName = :topicName
            """)
    List<FailedEvent> findByTopic(
            @Param("topicName")
            String topicName
    );

    // =====================================================
    // FIND BY SERVICE
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.serviceName = :serviceName
            ORDER BY f.createdAt DESC
            """)
    List<FailedEvent> findByService(
            @Param("serviceName")
            String serviceName
    );

    // =====================================================
    // FIND FAILED EVENTS
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.status = :status
            """)
    List<FailedEvent> findByStatus(
            @Param("status")
            String status
    );

    // =====================================================
    // COUNT FAILED EVENTS
    // =====================================================

    @Query("""
            SELECT COUNT(f)
            FROM FailedEvent f
            WHERE f.status = 'FAILED'
            """)
    Long countFailedEvents();

    // =====================================================
    // COUNT FAILED EVENTS BY SERVICE
    // =====================================================

    @Query("""
            SELECT COUNT(f)
            FROM FailedEvent f
            WHERE f.serviceName = :serviceName
            AND f.status = 'FAILED'
            """)
    Long countFailedEventsByService(
            @Param("serviceName")
            String serviceName
    );

    // =====================================================
    // FIND EVENTS BETWEEN DATES
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.createdAt BETWEEN :startDate AND :endDate
            ORDER BY f.createdAt DESC
            """)
    List<FailedEvent> findEventsBetweenDates(
            @Param("startDate")
            LocalDateTime startDate,

            @Param("endDate")
            LocalDateTime endDate
    );

    // =====================================================
    // FIND BY ERROR MESSAGE
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE LOWER(f.errorMessage)
            LIKE LOWER(CONCAT('%', :errorMessage, '%'))
            """)
    List<FailedEvent> searchByErrorMessage(
            @Param("errorMessage")
            String errorMessage
    );

    // =====================================================
    // FIND STUCK EVENTS
    // =====================================================

    @Query("""
            SELECT f
            FROM FailedEvent f
            WHERE f.retryCount >= :retryCount
            AND f.status = 'FAILED'
            """)
    List<FailedEvent> findStuckEvents(
            @Param("retryCount")
            Integer retryCount
    );

    // =====================================================
    // DELETE OLD EVENTS
    // =====================================================

    @Modifying
    @Transactional
    @Query("""
            DELETE
            FROM FailedEvent f
            WHERE f.createdAt < :createdAt
            """)
    void deleteOldEvents(
            @Param("createdAt")
            LocalDateTime createdAt
    );
}