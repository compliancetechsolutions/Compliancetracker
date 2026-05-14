package com.compliance.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ IMPORTANT
import org.springframework.stereotype.Repository;

import com.compliance.auth.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUser_UserId(UUID userId);

    // ✅ FIXED
    @Query("""
        SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
        FROM UserRole ur
        WHERE ur.user.userId = :userId
          AND ur.role.roleId = :roleId
    """)
    boolean existsMapping(@Param("userId") UUID userId,
                          @Param("roleId") UUID roleId);

    void deleteByUser_UserIdAndRole_RoleId(UUID userId, UUID roleId);

    void deleteByUser_UserId(UUID userId);
}