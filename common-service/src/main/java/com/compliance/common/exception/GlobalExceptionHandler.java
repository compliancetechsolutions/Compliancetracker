package com.compliance.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.compliance.common.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice

public class GlobalExceptionHandler {
	// ================= GENERIC =================
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntime(RuntimeException ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("BUSINESS_ERROR")
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ================= NOT FOUND =================
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("NOT_FOUND")
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ================= UNAUTHORIZED =================
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("UNAUTHORIZED")
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ================= VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex,
                                          HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return ErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .errorCode("VALIDATION_ERROR")
                .details(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ================= GLOBAL =================
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobal(Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
                .success(false)
                .message("Something went wrong")
                .errorCode("INTERNAL_SERVER_ERROR")
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
