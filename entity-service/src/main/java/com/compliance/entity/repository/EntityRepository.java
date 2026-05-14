package com.compliance.entity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.compliance.common.repository.BaseRepository;
import com.compliance.entity.entity.EntityMaster;

public interface EntityRepository
        extends BaseRepository<EntityMaster, UUID> {

    // =====================================================
    // DUPLICATE VALIDATION
    // =====================================================

    boolean existsByEntityNameIgnoreCaseAndIsDeletedFalse(
            String entityName);

    boolean existsByEntityNameIgnoreCaseAndEntityIdNotAndIsDeletedFalse(
            String entityName,
            UUID entityId);

    // =====================================================
    // BASIC ACTIVE FETCH
    // =====================================================

    List<EntityMaster> findByIsDeletedFalse();

    Page<EntityMaster> findByIsDeletedFalse(
            Pageable pageable);

    Optional<EntityMaster> findByEntityIdAndIsDeletedFalse(
            UUID entityId);

    // =====================================================
    // USER MAPPED ENTITIES
    // =====================================================

    @Query("""
        SELECT e
        FROM EntityMaster e
        JOIN EntityUserMapper m
          ON m.entityId = e.entityId
        WHERE m.userId = :userId
          AND m.isDeleted = false
          AND e.isDeleted = false
        ORDER BY e.createdAt DESC
    """)
    List<EntityMaster> findByUserId(
            @Param("userId") UUID userId);

    @Query("""
        SELECT e
        FROM EntityMaster e
        JOIN EntityUserMapper m
          ON m.entityId = e.entityId
        WHERE m.userId = :userId
          AND m.isDeleted = false
          AND e.isDeleted = false
    """)
    Page<EntityMaster> findByUserId(
            @Param("userId") UUID userId,
            Pageable pageable);

 // =====================================================
 // INVESTOR VIEW
 // =====================================================

    @Query("""
    	    SELECT DISTINCT e
    	    FROM EntityMaster e
    	    JOIN EntityUserMapper m
    	      ON m.entityId = e.entityId
    	    WHERE m.userId = :userId
    	      AND m.relationshipType LIKE '%INVESTOR%'
    	      AND m.isDeleted = false
    	      AND e.isDeleted = false
    	""")
    	Page<EntityMaster> findInvestorEntities(
    	        @Param("userId") UUID userId,
    	        Pageable pageable);
    // =====================================================
 // COMPANY REPRESENTATIVE VIEW
 // =====================================================

    @Query("""
    	    SELECT DISTINCT e
    	    FROM EntityMaster e
    	    JOIN EntityUserMapper m
    	      ON m.entityId = e.entityId
    	    WHERE m.userId = :userId
    	      AND (
    	            m.relationshipType LIKE '%REPRESENTATIVE%'
    	         OR m.relationshipType LIKE '%SIGNATORY%'
    	      )
    	      AND m.isDeleted = false
    	      AND e.isDeleted = false
    	""")
    	Page<EntityMaster> findRepresentativeEntities(
    	        @Param("userId") UUID userId,
    	        Pageable pageable);  // =====================================================
    // GENERIC ROLE FILTER
    // =====================================================

    @Query("""
    	    SELECT DISTINCT e
    	    FROM EntityMaster e
    	    JOIN EntityUserMapper m
    	      ON m.entityId = e.entityId
    	    WHERE m.userId = :userId
    	      AND m.roleId = :roleId
    	      AND m.isDeleted = false
    	      AND e.isDeleted = false
    	""")
    	List<EntityMaster> findByUserAndRole(
    	        @Param("userId") UUID userId,
    	        @Param("roleId") UUID roleId);

    // =====================================================
    // FAST ID LOOKUP
    // =====================================================

    @Query("""
        SELECT e.entityId
        FROM EntityMaster e
        JOIN EntityUserMapper m
          ON m.entityId = e.entityId
        WHERE m.userId = :userId
          AND m.isDeleted = false
          AND e.isDeleted = false
    """)
    List<UUID> findEntityIdsByUserId(
            @Param("userId") UUID userId);

    // =====================================================
    // SAFE SINGLE FETCH
    // =====================================================

    @Query("""
        SELECT e
        FROM EntityMaster e
        WHERE e.entityId = :entityId
          AND e.isDeleted = false
    """)
    Optional<EntityMaster> findActiveById(
            @Param("entityId") UUID entityId);
}