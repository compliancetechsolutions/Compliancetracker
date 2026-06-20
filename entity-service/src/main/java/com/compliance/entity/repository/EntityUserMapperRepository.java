package com.compliance.entity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.compliance.common.repository.BaseRepository;
import com.compliance.entity.entity.EntityUserMapper;

public interface EntityUserMapperRepository extends BaseRepository<EntityUserMapper, UUID> {

  // =====================================================
  // BASIC ACTIVE FETCH
  // =====================================================

  List<EntityUserMapper> findByUserIdAndIsDeletedFalse(UUID userId);

  List<EntityUserMapper> findByEntityIdAndIsDeletedFalse(UUID entityId);

  Page<EntityUserMapper> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

  // =====================================================
  // ACCESS CHECK (RBAC)
  // =====================================================

  boolean existsByUserIdAndEntityIdAndIsDeletedFalse(UUID userId, UUID entityId);

  Optional<EntityUserMapper> findFirstByUserIdAndEntityIdAndIsDeletedFalse(UUID userId, UUID entityId);

  // =====================================================
  // ROLE BASED FETCH
  // =====================================================

  @Query("""
          SELECT m
          FROM EntityUserMapper m
          WHERE m.userId = :userId
            AND m.relationshipType = :role
            AND m.isDeleted = false
      """)
  List<EntityUserMapper> findByUserAndRole(@Param("userId") UUID userId, @Param("role") String role);

  @Query("""
          SELECT m
          FROM EntityUserMapper m
          WHERE m.userId = :userId
            AND m.relationshipType = :role
            AND m.isDeleted = false
      """)
  Page<EntityUserMapper> findByUserAndRole(@Param("userId") UUID userId, @Param("role") String role,
      Pageable pageable);

  // =====================================================
  // FAST ENTITY ID LOOKUP
  // =====================================================

  @Query("""
          SELECT m.entityId
          FROM EntityUserMapper m
          WHERE m.userId = :userId
            AND m.isDeleted = false
      """)
  List<UUID> findEntityIdsByUserId(@Param("userId") UUID userId);

  @Query("""
          SELECT m.entityId
          FROM EntityUserMapper m
          WHERE m.userId = :userId
            AND m.relationshipType = :role
            AND m.isDeleted = false
      """)
  List<UUID> findEntityIdsByUserAndRole(@Param("userId") UUID userId, @Param("role") String role);

  // =====================================================
  // DELETE / REMAP HELPERS
  // =====================================================

  long countByEntityIdAndIsDeletedFalse(UUID entityId);

  long countByUserIdAndIsDeletedFalse(UUID userId);

  boolean existsByEntityIdAndUserIdAndRoleIdAndIsDeletedFalse(UUID entityId, UUID userId, UUID roleId);

  boolean existsByEntityIdAndUserIdAndIsDeletedFalse(UUID entityId, UUID userId);

  

}