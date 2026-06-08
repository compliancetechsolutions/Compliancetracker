package com.compliance.entity.repository;

import java.util.Optional;
import java.util.UUID;

import com.compliance.common.repository.BaseRepository;
import com.compliance.entity.entity.EntityType;

public interface EntityTypeRepository
        extends BaseRepository<EntityType, UUID> {

    // =====================================================
    // FIND BY TYPE NAME
    // =====================================================

    Optional<EntityType>
    findByTypeNameIgnoreCaseAndIsDeletedFalse(
            String typeName
    );

    // =====================================================
    // FIND BY DESCRIPTION
    // =====================================================

    Optional<EntityType>
    findByDescriptionIgnoreCaseAndIsDeletedFalse(
            String description
    );
}