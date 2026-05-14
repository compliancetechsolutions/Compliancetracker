package com.compliance.entity.dto;

import java.util.List;

import lombok.Data;
@Data
public class BulkEntityRequest {
	
    private List<EntityRequest> entities;


}
