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
@Table(name = "entity_locations", schema = "entity_schema")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityLocation extends BaseEntity {
	@Id
	@Column(name = "location_id")
	private UUID locationId;

	@Column(name = "entity_id")
	private UUID entityId;

	private String address;
	private String city;
	private String country;

}
