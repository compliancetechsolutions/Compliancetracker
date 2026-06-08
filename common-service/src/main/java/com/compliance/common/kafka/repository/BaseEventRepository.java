package com.compliance.common.kafka.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.compliance.common.kafka.event.BaseEvent;
import com.compliance.common.repository.BaseRepository;

@NoRepositoryBean
public interface BaseEventRepository<
        T extends BaseEvent
        >
        extends BaseRepository<
                T,
                UUID
                > {

    // =====================================================
    // EVENT TYPE
    // =====================================================

    List<T> findByEventType(
            String eventType
    );

    // =====================================================
    // STATUS
    // =====================================================

    List<T> findByStatus(
            String status
    );

    // =====================================================
    // AGGREGATE ID
    // =====================================================

    List<T> findByAggregateId(
            UUID aggregateId
    );

    // =====================================================
    // SERVICE NAME
    // =====================================================

    List<T> findByServiceName(
            String serviceName
    );

    // =====================================================
    // RETRY COUNT
    // =====================================================

    List<T> findByRetryCountLessThan(
            Integer retryCount
    );
}