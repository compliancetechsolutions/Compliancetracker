package com.compliance.compliance.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.compliance.compliance.dto.ComplianceRequest;
import com.compliance.compliance.dto.ComplianceResponse;
import com.compliance.compliance.entity.ComplianceRecord;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComplianceMapper {

  // ===============================
  // REQUEST → ENTITY
  // ===============================

  @Mapping(target = "complianceId", ignore = true)

  @Mapping(target = "actualCompletionDate", ignore = true)

  @Mapping(target = "notificationSent", ignore = true)

  @Mapping(target = "notificationRetryCount", ignore = true)

  @Mapping(target = "compliant", ignore = true)

  @Mapping(target = "overdue", ignore = true)

  @Mapping(target = "triggeredRule", ignore = true)

  @Mapping(target = "ruleResult", ignore = true)

  ComplianceRecord toEntity(ComplianceRequest request);

  // ===============================
  // ENTITY → RESPONSE
  // ===============================

  @Mapping(target = "completedDate",

      source = "actualCompletionDate")

  @Mapping(target = "executionId",

      ignore = true)

  @Mapping(target = "message",

      ignore = true)

  ComplianceResponse toResponse(ComplianceRecord entity);

  // ===============================
  // UPDATE
  // ===============================

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

  @Mapping(target = "complianceId",

      ignore = true)

  @Mapping(target = "actualCompletionDate",

      ignore = true)

  @Mapping(target = "notificationSent",

      ignore = true)

  @Mapping(target = "notificationRetryCount",

      ignore = true)

  @Mapping(target = "compliant",

      ignore = true)

  @Mapping(target = "overdue",

      ignore = true)

  @Mapping(target = "triggeredRule",

      ignore = true)

  @Mapping(target = "ruleResult",

      ignore = true)

  void update(ComplianceRequest request,

      @MappingTarget ComplianceRecord entity);

}