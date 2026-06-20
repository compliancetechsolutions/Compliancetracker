package com.compliance.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.compliance.auth.dto.LoginRequestDto;
import com.compliance.auth.dto.LoginResponseDto;
import com.compliance.auth.dto.RefreshRequestDto;
import com.compliance.auth.service.AuthService;
import com.compliance.common.controller.BaseController;
import com.compliance.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Authentication endpoints.
 *
 * <p><b>Security note:</b> refresh and logout tokens are passed in the request
 * BODY — never as query parameters. Query params are logged by web servers,
 * proxies, CDNs, and browser history, which would expose sensitive tokens.
 */
@Tag(name = "Auth", description = "Login, token refresh and logout")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * Authenticate and issue access + refresh tokens.
     * Returns 201 Created to signal a new session resource was created.
     */
    @Operation(summary = "Login — returns access and refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {
      System.out.println("LOGIN API HIT");
        LoginResponseDto response = authService.login(request, ip);
        return created("Login successful", response);
    }

    /**
     * Exchange a valid refresh token for a new access + refresh token pair.
     * The old refresh token is revoked (rotation).
     *
     * <p>Token is passed in the body to prevent leakage in access logs.
     */
    @Operation(summary = "Refresh access token using a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refresh(
            @Valid @RequestBody RefreshRequestDto request) {

        LoginResponseDto response = authService.refresh(request.getRefreshToken());
        return ok("Token refreshed", response);
    }

    /**
     * Revoke the refresh token and invalidate all active sessions for the user.
     *
     * <p>Token is passed in the body — see note on /refresh.
     */
    @Operation(summary = "Logout — revokes refresh token and all sessions")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshRequestDto request) {

        authService.logout(request.getRefreshToken());
        return ok("Logged out successfully");
    }
   
}
