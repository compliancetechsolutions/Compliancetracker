package com.compliance.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compliance.auth.entity.User;
import com.compliance.common.repository.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    // ✅ Basic queries
    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username); // 👈 ADD THIS (optional but safe)

    Optional<User> findByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    // 🔥 roles + permissions (login)
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.userRoles ur
        LEFT JOIN FETCH ur.role r
        LEFT JOIN FETCH r.rolePermissions rp
        LEFT JOIN FETCH rp.permission
        WHERE LOWER(u.username) = LOWER(:username)
          AND u.isDeleted = false
    """)
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    // 🔥 roles only
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.userRoles ur
        LEFT JOIN FETCH ur.role r
        WHERE u.userId = :userId
          AND u.isDeleted = false
    """)
    Optional<User> findByIdWithRoles(@Param("userId") UUID userId);
}