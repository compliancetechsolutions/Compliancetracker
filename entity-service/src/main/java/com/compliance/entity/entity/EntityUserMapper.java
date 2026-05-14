package com.compliance.entity.entity;

import com.compliance.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "entity_user_mapper",
    schema = "entity_schema",
    indexes = {
        @Index(name = "idx_eum_entity_id", columnList = "entity_id"),
        @Index(name = "idx_eum_user_id",   columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_entity_user", columnNames = {"entity_id", "user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@SuperBuilder
//@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class EntityUserMapper extends BaseEntity {

    @Id
    @Column(name = "entity_user_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID entityUserId;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "investment_amount", precision = 18, scale = 2)
    private BigDecimal investmentAmount;

    @Column(name = "ownership_percentage", precision = 5, scale = 2)
    private BigDecimal ownershipPercentage;

    @Column(name = "relationship_type", length = 100)
    private String relationshipType;
}
