package com.compliance.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityResponse {

	private UUID entityId;
	private String entityName;
	private UUID entityTypeId;
	private String registrationNumber;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdBy;
}
