package com.compliance.entity.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.compliance.entity.dto.EntityRequest;
import com.compliance.entity.dto.EntityResponse;
import com.compliance.entity.entity.EntityMaster;

/**
 * MapStruct mapper for EntityMaster ↔ DTO conversion.
 *
 * <p>Using MapStruct instead of hand-written mappers gives us:
 * <ul>
 *   <li>Compile-time safety — mapping errors surface at build time.</li>
 *   <li>No reflection overhead at runtime.</li>
 *   <li>Partial update support via {@code @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)}.</li>
 * </ul>
 */
@Mapper(
    componentModel  = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE   // suppress warnings for audit fields set by JPA
)
public interface EntityMapper {

    EntityMaster toEntity(EntityRequest request);

    EntityResponse toResponse(EntityMaster entity);

    /**
     * Applies only non-null fields from {@code request} onto the existing {@code entity}.
     * Used for PATCH-style partial updates.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(EntityRequest request, @MappingTarget EntityMaster entity);
}
