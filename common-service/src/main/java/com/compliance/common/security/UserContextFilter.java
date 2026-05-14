package com.compliance.common.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/auth/login", "/auth/refresh", "/swagger-ui", "/v3/api-docs", "/actuator");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userId     = request.getHeader("X-User-Id");
        String username   = request.getHeader("X-Username");
        String rolesHeader = request.getHeader("X-Roles");

        if (userId != null && username != null && rolesHeader != null) {

            List<String> roles = Arrays.asList(rolesHeader.split(","));

            var authorities = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // principal = userId (UUID string) — required so UserContext.getUserId()
            // can do UUID.fromString(auth.getName()) without throwing.
            // username stored in credentials slot for UserContext.getUsername().
            var auth = new UsernamePasswordAuthenticationToken(userId, username, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("UserContextFilter: authenticated userId={} roles={}", userId, roles);

        } else {
            log.warn("UserContextFilter: missing gateway headers on path={} — no authentication set",
                    request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}