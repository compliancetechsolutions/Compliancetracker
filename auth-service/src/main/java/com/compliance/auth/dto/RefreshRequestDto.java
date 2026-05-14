package com.compliance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request body for /auth/refresh and /auth/logout.
 *
 * <p>The refresh token is passed in the request body — NEVER as a query
 * parameter, because query params appear in:
 * <ul>
 *   <li>Web server access logs (Nginx, Apache)</li>
 *   <li>Reverse proxy and CDN logs</li>
 *   <li>Browser history</li>
 *   <li>HTTP Referer headers on subsequent requests</li>
 * </ul>
 */
@Getter
@NoArgsConstructor
public class RefreshRequestDto {

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
