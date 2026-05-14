package com.compliance.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private boolean success;
	private String message;
	private int status;
	private String errorCode;
	private List<String> details;
	private String path;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	// ===== BUILDER =====
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final ErrorResponse response = new ErrorResponse();

		public Builder success(boolean success) {
			response.success = success;
			return this;
		}

		public Builder message(String message) {
			response.message = message;
			return this;
		}

		public Builder status(int status) {
			response.status = status;
			return this;
		}

		public Builder errorCode(String errorCode) {
			response.errorCode = errorCode;
			return this;
		}

		public Builder details(List<String> details) {
			response.details = details;
			return this;
		}

		public Builder path(String path) {
			response.path = path;
			return this;
		}

		public Builder timestamp(LocalDateTime timestamp) {
			response.timestamp = timestamp;
			return this;
		}

		public ErrorResponse build() {
			if (response.timestamp == null) {
				response.timestamp = LocalDateTime.now();
			}
			return response;
		}
	}

	// ===== GETTERS =====
	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public List<String> getDetails() {
		return details;
	}

	public String getPath() {
		return path;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}