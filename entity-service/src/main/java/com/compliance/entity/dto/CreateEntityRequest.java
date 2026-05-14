package com.compliance.entity.dto;

import com.compliance.common.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateEntityRequest extends BaseDto {

    private String entityName;

    private UUID entityTypeId;

    private String registrationNumber;

    private List<EntityUserMappingRequest> users;
}