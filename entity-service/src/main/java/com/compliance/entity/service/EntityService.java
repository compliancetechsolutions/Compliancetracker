package com.compliance.entity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.compliance.common.service.BaseService;
import com.compliance.entity.dto.BulkEntityRequest;
import com.compliance.entity.dto.BulkEntityResponse;
import com.compliance.entity.dto.EntityBulkUserMappingRequest;
import com.compliance.entity.dto.EntityRequest;
import com.compliance.entity.dto.EntityResponse;
import com.compliance.entity.dto.EntityUserMappingRequest;

public interface EntityService extends BaseService<EntityRequest, EntityRequest, EntityResponse, UUID> {

	// Current logged-in user
	List<EntityResponse> getMyEntities();
	void mapUser(UUID entityId, EntityUserMappingRequest request);
	// Role-based views (user comes from UserContext)
	Page<EntityResponse> getInvestorEntities(Pageable pageable);
	Page<EntityResponse> getRepresentativeEntities(Pageable pageable);
	void mapUsers(UUID entityId, EntityBulkUserMappingRequest request);
	public BulkEntityResponse createEntities(
	        BulkEntityRequest request);
	

}