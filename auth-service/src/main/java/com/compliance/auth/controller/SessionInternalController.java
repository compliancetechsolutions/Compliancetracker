package com.compliance.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.auth.repository.UserSessionRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/session")
@RequiredArgsConstructor
public class SessionInternalController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserSessionRepository sessionRepo;

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateSession(
            @RequestHeader("Authorization") String authHeader) {

        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith(BEARER_PREFIX)) {

            return ResponseEntity.ok(false);
        }

        String token =
                authHeader.substring(BEARER_PREFIX.length());

        boolean active =
                sessionRepo.existsByJwtTokenAndActiveTrue(token);

        return ResponseEntity.ok(active);
    }
}