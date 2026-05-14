package com.compliance.common.controller;

import com.compliance.common.dto.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public abstract class BaseController {

	protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
		return ResponseEntity.ok(buildSuccess("Success", data, 200));
	}

	protected <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(buildSuccess(message, data, 200));
	}

	protected <T> ResponseEntity<ApiResponse<T>> ok(String message) {
		return ResponseEntity.ok(ApiResponse.<T>builder().success(true).message(message).status(200)
				.timestamp(LocalDateTime.now()).build());
	}

	protected <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
		return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccess(message, data, 201));
	}

	protected ResponseEntity<Void> deleted() {
		return ResponseEntity.noContent().build();
	}

	protected ResponseEntity<Void> noContent() {
		return ResponseEntity.noContent().build();
	}

	private <T> ApiResponse<T> buildSuccess(String message, T data, int status) {
		return ApiResponse.<T>builder().success(true).message(message).status(status).data(data)
				.timestamp(LocalDateTime.now()).build();
	}
}