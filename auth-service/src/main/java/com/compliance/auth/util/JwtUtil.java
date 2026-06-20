package com.compliance.auth.util;

import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.compliance.common.constants.AppConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiry-ms:86400000}")
  private long accessTokenExpiryMs;

  @Value("${jwt.refresh-token-expiry-days:7}")
  private int refreshTokenExpiryDays;

  private SecretKey signingKey;

  @PostConstruct
  public void init() {
    Assert.hasLength(secret, "jwt.secret must not be blank");
    Assert.isTrue(secret.getBytes(StandardCharsets.UTF_8).length >= 32,
        "jwt.secret must be at least 256 bits (32 bytes)");
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    log.info("JWT signing key initialised");
  }

  public String generateAccessToken(String username, UUID userId, List<String> roles) {
    Date now = new Date();
    return Jwts.builder().subject(username).claim(AppConstants.CLAIM_USER_ID, userId.toString())
        .claim(AppConstants.CLAIM_ROLES, roles).issuedAt(now)
        .expiration(new Date(now.getTime() + accessTokenExpiryMs)).signWith(signingKey).compact();
  }

  public String generateRefreshToken(String username) {
    Date now = new Date();
    long expiryMs = (long) refreshTokenExpiryDays * 24L * 60 * 60 * 1000;
    return Jwts.builder().subject(username).issuedAt(now).expiration(new Date(now.getTime() + expiryMs))
        .signWith(signingKey).compact();
  }


  public Claims validate(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  public String extractUsername(String token) {
    return validate(token).getSubject();
  }

  public UUID extractUserId(String token) {
    return UUID.fromString(validate(token).get(AppConstants.CLAIM_USER_ID, String.class));
  }

  public List<String> extractRoles(String token) {
    Object roles = validate(token).get(AppConstants.CLAIM_ROLES);
    if (roles instanceof List<?> list) {
      return list.stream().map(Object::toString).toList();
    }
    return List.of();
  }

  public boolean isTokenExpired(String token) {
    try {
      return validate(token).getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
      log.warn("Invalid JWT during expiry check: {}", e.getMessage());
      return true;
    }
  }
   public long getAccessTokenExpirySeconds() {
          return accessTokenExpiryMs / 1000;
      }

      // 🔥 OPTIONAL (dynamic remaining time)
      public long getRemainingSeconds(String token) {
          Date exp = validate(token).getExpiration();
          return (exp.getTime() - System.currentTimeMillis()) / 1000;
      }
}