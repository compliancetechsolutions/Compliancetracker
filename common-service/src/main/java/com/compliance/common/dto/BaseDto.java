package com.compliance.common.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class BaseDto {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected LocalDateTime updatedAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String createdBy;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String updatedBy;

}
