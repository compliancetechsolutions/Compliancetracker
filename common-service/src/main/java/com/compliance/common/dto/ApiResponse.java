package com.compliance.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Uniform API response envelope used across all services.
 *
 * <p>{@code @JsonInclude(NON_NULL)} suppresses null fields in the JSON output
 * so error responses don't carry empty {@code "data": null} and success
 * responses don't carry {@code "errorCode": null}.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean        success;
    private final String         message;
    private final String         errorCode;   // present only on errors
    private final int            status;
    private final T              data;        // present only on success
    private final List<String>   details;     // validation error list
    private final String         path;
    private final LocalDateTime  timestamp;

    // ── Static factories ─────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(200)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(201)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode,
                                           int status, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> validationError(String message,
                                                      List<String> details,
                                                      String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .status(400)
                .details(details)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
