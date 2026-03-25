package com.compliance.common.controller;

import org.springframework.http.ResponseEntity;

import com.compliance.common.dto.ApiResponse;

public abstract class BaseController {
	protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
		return ResponseEntity.ok(ApiResponse.success("Success", data));
	}

	protected <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(ApiResponse.success(message, data));
	}

	protected <T> ResponseEntity<ApiResponse<T>> error(String message) {
		return ResponseEntity.badRequest().body(ApiResponse.error(message));
	}
}
