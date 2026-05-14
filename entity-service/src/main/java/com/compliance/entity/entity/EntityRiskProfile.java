package com.compliance.entity.entity;

import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entity_risk_profiles", schema = "entity_schema")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityRiskProfile extends BaseEntity {
	@Id
	@Column(name = "risk_profile_id")
	private UUID riskProfileId;

	@Column(name = "entity_id")
	private UUID entityId;

	@Column(name = "risk_level")
	private String riskLevel;

	@Column(name = "risk_score")
	private Double riskScore;

}
