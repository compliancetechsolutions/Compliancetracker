package com.compliance.entity.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class EntityUserMappingRequest {

    private UUID userId;

    private UUID roleId;

    private BigDecimal ownershipPercentage;
    private BigDecimal investmentAmount;


    private String relationshipType;
}