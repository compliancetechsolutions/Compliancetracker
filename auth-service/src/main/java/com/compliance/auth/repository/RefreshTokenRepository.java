package com.compliance.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);

  // Get all tokens for a user
  List<RefreshToken> findByUserId(UUID userId);

  // Active tokens
  List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);

  // Delete expired tokens
  void deleteByExpiryTimeBefore(LocalDateTime time);

  // Revoke all tokens of user
  List<RefreshToken> findByUserIdAndRevokedFalseAndExpiryTimeAfter(UUID userId, LocalDateTime now);

}
