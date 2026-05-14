package com.compliance.common.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.compliance.common.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralised exception handler.
 *
 * <p>
 * <b>Key fix from original:</b> {@link BaseException} now uses the HTTP status
 * from its {@code ErrorCode} instead of always returning 400 BAD_REQUEST.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// ── Domain exceptions ────────────────────────────────────────────────────

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {

		log.warn("Not found: {} path={}", ex.getMessage(), req.getRequestURI());
		return build(ex.getMessage(), "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, req, null);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
		return ResponseEntity.status(409).body(ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {

		log.warn("Unauthorized: {} path={}", ex.getMessage(), req.getRequestURI());

		// 🔥 ALWAYS return 401 for login/auth failures
		return build(
			    ex.getMessage(),
			    ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "UNAUTHORIZED",
			    HttpStatus.UNAUTHORIZED,
			    req,
			    null
			);
	}

	/**
	 * Handles all {@link BaseException} subtypes. Uses the HTTP status stored on
	 * the {@code ErrorCode} — NOT a hardcoded 400.
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusiness(BaseException ex, HttpServletRequest req) {

		log.warn("Business exception: {} code={} path={}", ex.getMessage(), ex.getErrorCode(), req.getRequestURI());

		HttpStatus status = ex.getErrorCode() != null ? ex.getErrorCode().getStatus() : HttpStatus.BAD_REQUEST;

		return build(ex.getMessage(), ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "BUSINESS_ERROR",
				status, req, null);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUsernameNotFound(UsernameNotFoundException ex,
			HttpServletRequest req) {

		return build("Invalid username or password", "AUTH_INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, req, null);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {

		return build("Invalid username or password", "AUTH_INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, req, null);
	}

	// ── Validation ───────────────────────────────────────────────────────────

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest req) {

		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(field -> field.getField(), field -> field.getDefaultMessage(), (e1, e2) -> e1 // handle
																														// duplicate
																														// keys
				));

		log.warn("Validation failed: {} path={}", errors, req.getRequestURI());

		return build("Validation failed", "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, req, errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest req) {

		List<String> errors = ex.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + ": " + v.getMessage()).sorted().toList();

		log.warn("Constraint violation: {} path={}", errors, req.getRequestURI());

		return build("Validation failed", "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, req, errors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<List<String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest req) {

		String detail = String.format("Parameter '%s' has invalid value: %s", ex.getName(), ex.getValue());

		return build(detail, "TYPE_MISMATCH", HttpStatus.BAD_REQUEST, req, List.of(detail));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest req) {

		return build("Required parameter '" + ex.getParameterName() + "' is missing", "MISSING_PARAMETER",
				HttpStatus.BAD_REQUEST, req, null);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex,
			HttpServletRequest req) {

		return build("Request body is malformed or missing", "MALFORMED_REQUEST", HttpStatus.BAD_REQUEST, req, null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest req) {

		return build(ex.getMessage(), "METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, req, null);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleMediaType(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest req) {

		return build(ex.getMessage(), "UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, req, null);
	}

	// ── Catch-all ────────────────────────────────────────────────────────────

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGlobal(Exception ex, HttpServletRequest req) {

		// Log full stack trace for unexpected errors
		log.error("Unexpected error path={}", req.getRequestURI(), ex);
		return build("An unexpected error occurred. Please try again later.", "INTERNAL_SERVER_ERROR",
				HttpStatus.INTERNAL_SERVER_ERROR, req, null);
	}

	// ── Builder ──────────────────────────────────────────────────────────────

	private <T> ResponseEntity<ApiResponse<T>> build(String message, String errorCode, HttpStatus status,
			HttpServletRequest req, T data) {

		ApiResponse<T> body = ApiResponse.<T>builder().success(false).message(message).errorCode(errorCode)
				.status(status.value()).data(data) // 🔥 use data instead of details
				.path(req.getRequestURI()).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(status).body(body);
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handle404(
	        NoResourceFoundException ex,
	        HttpServletRequest req) {

	    return build(
	            "API endpoint not found",
	            "NOT_FOUND",
	            HttpStatus.NOT_FOUND,
	            req,
	            null
	    );
	}
	
}
