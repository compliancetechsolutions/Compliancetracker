package com.compliance.entity.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EntityBulkUserMappingRequest {

	 @NotEmpty(message = "Users list cannot be empty")
	    private List<@Valid EntityUserMappingRequest> users;

}
