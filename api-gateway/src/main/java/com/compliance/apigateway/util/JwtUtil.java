package com.compliance.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    // ✅ FIX: Removed dependency on AppConstants
    private static final String CLAIM_ROLES = "roles";

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        Assert.hasLength(secret, "jwt.secret must not be blank — set JWT_SECRET env variable");
        Assert.isTrue(
                secret.getBytes(StandardCharsets.UTF_8).length >= 32,
                "jwt.secret must be at least 256 bits (32 bytes)"
        );

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("🔥 Gateway JWT signing key initialized");
    }

    /**
     * Parses and validates the token signature and expiry.
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract roles from claims
     */
    public List<String> extractRolesFromClaims(Claims claims) {
        Object roles = claims.get(CLAIM_ROLES);

        if (roles instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }

    /**
     * Validate token
     */
    public boolean isValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new java.util.Date());
        } catch (ExpiredJwtException e) {
            log.warn("❌ Token expired");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}