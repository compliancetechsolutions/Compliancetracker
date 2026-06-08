package com.compliance.entity.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.compliance.common.enums.EntityStatus;
import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;






/**
 * Core entity record — companies, funds, or individuals subject to compliance.
 *
 * <p><b>Fix: status field changed from {@code String} to {@link EntityStatus} enum.</b>
 *
 * <p>The original used a plain {@code String} for status with magic values
 * "ACTIVE", "INACTIVE", "SUSPENDED". This creates several problems:
 * <ul>
 *   <li>No compile-time safety — any string can be set, including typos.</li>
 *   <li>No canonical list of valid values — developers must search the codebase.</li>
 *   <li>The {@code EntityStatus} enum already exists in common-service and
 *       goes unused.</li>
 * </ul>
 *
 * <p>{@code @Enumerated(EnumType.STRING)} persists the enum name as a varchar,
 * which is readable in the DB and survives reordering of enum constants (unlike
 * {@code EnumType.ORDINAL}).
 */
@Entity
@Table(
    name = "entities",
    schema = "entity_schema",
    indexes = {
        @Index(name = "idx_entities_name",   columnList = "entity_name"),
        @Index(name = "idx_entities_status", columnList = "status"),
        @Index(name = "idx_entities_type",   columnList = "entity_type_id")
    }
)
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class EntityMaster extends BaseEntity {

    @Id
    @Column(name = "entity_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID entityId;

    @Column(name = "entity_name", nullable = false, length = 200)
    private String entityName;

    @Column(name = "entity_type_id")
    private UUID entityTypeId;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    /**
     * Entity lifecycle status.
     *
     * <p>FIXED: was {@code String status = "ACTIVE"} — no compile-time safety.
     * Now uses the {@link EntityStatus} enum defined in common-service.
     * Persisted as a varchar for DB readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column(name = "company_start_date")
    private LocalDate companyStartDate;

    /**
     * Total number of employees in the organization.
     */
    @Column(name = "no_of_employees")
    private Integer noOfEmployees;
}

