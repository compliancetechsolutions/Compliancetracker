package com.compliance.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.compliance.apigateway.config.RouteRoleConfig;
import com.compliance.apigateway.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RouteRoleConfig routeRoleConfig;
    private final WebClient webClient;

    @PostConstruct
    public void init() {
        log.info("JwtAuthenticationFilter loaded — order={}", getOrder());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("JWT filter: {} {}", request.getMethod(), path);

        if (isPublicPath(path)) {
            log.debug("Public path — skipping JWT filter: {}", path);
            return chain.filter(exchange);
        }

        exchange.getResponse()
                .getHeaders()
                .addIfAbsent("X-Content-Type-Options", "nosniff");

        exchange.getResponse()
                .getHeaders()
                .addIfAbsent("X-Frame-Options", "DENY");

        String authHeader =
                request.getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null
                || !authHeader.startsWith(BEARER_PREFIX)) {

            log.warn(
                    "Missing/malformed Authorization header for protected path: {}",
                    path);

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "AUTH_REQUIRED",
                    "Authentication required");
        }

        String token =
                authHeader.substring(BEARER_PREFIX.length());

        Claims claims;

        try {

            claims = jwtUtil.parseToken(token);

        } catch (ExpiredJwtException ex) {

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "TOKEN_EXPIRED",
                    "Token has expired");

        } catch (JwtException ex) {

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "TOKEN_INVALID",
                    "Invalid token");
        }

        String userId =
                claims.get("userId", String.class);

        String username =
                claims.getSubject();

        List<String> roles =
                jwtUtil.extractRolesFromClaims(claims);

        log.debug(
                "JWT valid — userId={} username={} roles={}",
                userId,
                username,
                roles);

        // VALIDATE CLAIMS
        if (userId == null
                || username == null
                || roles == null
                || roles.isEmpty()) {

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_CLAIMS",
                    "Invalid token claims");
        }

        // SESSION VALIDATION (Reactive)
        return webClient.get()
                .uri("http://auth-service/internal/session/validate/" + userId)
                .retrieve()
                .bodyToMono(Boolean.class)

                .flatMap(activeSession -> {

                    if (Boolean.FALSE.equals(activeSession)) {

                        log.warn(
                                "Inactive session for userId={}",
                                userId);

                        return writeError(
                                exchange,
                                HttpStatus.UNAUTHORIZED,
                                "SESSION_EXPIRED",
                                "Session expired or logged out");
                    }

                    // RBAC enforcement
                    List<String> allowedRoles =
                            routeRoleConfig.getAllowedRoles(path);

                    if (!allowedRoles.isEmpty()) {

                        boolean allowed =
                                roles.stream()
                                        .anyMatch(allowedRoles::contains);

                        if (!allowed) {

                            log.warn(
                                    "Access denied — userId={} roles={} path={}",
                                    userId,
                                    roles,
                                    path);

                            return writeError(
                                    exchange,
                                    HttpStatus.FORBIDDEN,
                                    "ACCESS_DENIED",
                                    "You do not have permission to access this resource");
                        }
                    }

                    ServerHttpRequest mutatedRequest =
                            exchange.getRequest()
                                    .mutate()
                                    .header("X-User-Id", userId)
                                    .header("X-Username", username)
                                    .header("X-Roles", String.join(",", roles))
                                    .build();

                    return chain.filter(
                            exchange.mutate()
                                    .request(mutatedRequest)
                                    .build());
                })

                .onErrorResume(ex -> {

                    log.error(
                            "Session validation failed for userId={}",
                            userId,
                            ex);

                    return writeError(
                            exchange,
                            HttpStatus.UNAUTHORIZED,
                            "SESSION_VALIDATION_FAILED",
                            "Unable to validate session");
                });
    }

    private boolean isPublicPath(String path) {

        if (path == null)
            return false;

        String p = path.toLowerCase();

        return p.contains("/v3/api-docs")
                || p.contains("/swagger-ui")
                || p.contains("/webjars")
                || p.startsWith("/auth/login")
                || p.startsWith("/auth/refresh")
                || p.equals("/actuator/health")
                || p.equals("/actuator/info");
    }

    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String code,
            String message) {

        ServerHttpResponse response =
                exchange.getResponse();

        response.setStatusCode(status);

        response.getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "success", false,
                "status", status.value(),
                "errorCode", code,
                "message", message,
                "timestamp", Instant.now().toString(),
                "path", exchange.getRequest().getURI().getPath());

        try {

            byte[] bytes =
                    objectMapper.writeValueAsBytes(body);

            DataBuffer buffer =
                    response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {

            byte[] fallback =
                    ("{\"error\":\"" + message + "\"}")
                            .getBytes(StandardCharsets.UTF_8);

            return response.writeWith(
                    Mono.just(
                            response.bufferFactory()
                                    .wrap(fallback)));
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }
}