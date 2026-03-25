package com.compliance.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public abstract class BaseDto {
	private UUID id;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String createdBy;
	private String updatedBy;

}
