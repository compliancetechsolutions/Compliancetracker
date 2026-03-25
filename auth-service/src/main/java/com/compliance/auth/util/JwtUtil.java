package com.compliance.auth.util;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET =
            "compliance-secret-compliance-secret-compliance-secret";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // =========================
    // ACCESS TOKEN
    // =========================
    public String generateAccessToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(key)
                .compact();
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    // =========================
    // VALIDATE TOKEN
    // =========================
    public Claims validate(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // EXTRACT USERNAME
    // =========================
    public String extractUsername(String token) {
        return validate(token).getSubject();
    }

    // =========================
    // EXTRACT ROLES
    // =========================
    public List<String> extractRoles(String token) {

        Object roles = validate(token).get("roles");

        if (roles instanceof List<?>) {
            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }

    // =========================
    // CHECK EXPIRY
    // =========================
    public boolean isTokenExpired(String token) {

        try {
            return validate(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // =========================
    // OPTIONAL: ROLE → SCOPES
    // =========================
    public List<String> getScopes(String role) {
        return switch (role) {
            case "ADMIN" -> List.of("system:write", "user:manage", "activity:all");
            case "INVESTOR" -> List.of("report:read", "dashboard:view");
            case "INITIATOR" -> List.of("activity:update", "tracker:read");
            default -> List.of();
        };
    }
}