package com.compliance.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
  Optional<UserSession> findByJwtToken(String token);
  List<UserSession> findByUserIdAndActiveTrue(UUID userId);
  List<UserSession> findByUserId(UUID userId);
  boolean existsByUserIdAndActiveTrue(UUID userId);
  boolean existsByJwtTokenAndActiveTrue(String jwtToken);
  
}


