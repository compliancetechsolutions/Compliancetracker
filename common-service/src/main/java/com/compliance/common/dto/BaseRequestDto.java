package com.compliance.common.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)

public abstract class BaseRequestDto extends BaseDto {
	private String requestId;

	// 🔥 Who is making the request (optional - from gateway)
	private String requestedBy;

	// 🔥 Timestamp of request
	private LocalDateTime requestTime;
}
