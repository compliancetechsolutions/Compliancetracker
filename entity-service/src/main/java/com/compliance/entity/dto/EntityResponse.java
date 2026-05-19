package com.compliance.entity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

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
	private LocalDate companyStartDate;
	private Integer noOfEmployees;
}
