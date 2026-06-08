package com.compliance.entity.kafka.service;

import com.compliance.common.kafka.event.UserEvent;

public interface EntityEventService {

    // =====================================================
    // USER CREATED
    // =====================================================

    void handleUserCreated(
            UserEvent event
    );

    // =====================================================
    // USER DELETED
    // =====================================================

    void handleUserDeleted(
            UserEvent event
    );

    // =====================================================
    // USER ROLE UPDATED
    // =====================================================

    void handleUserRoleUpdated(
            UserEvent event
    );
}