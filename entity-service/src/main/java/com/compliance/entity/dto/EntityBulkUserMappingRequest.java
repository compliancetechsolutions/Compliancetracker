package com.compliance.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class EntityBulkUserMappingRequest {

	private List<EntityUserMappingRequest> users;

}
