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
@Table(name = "entity_contacts", schema = "entity_schema")
@EqualsAndHashCode(callSuper = true)
@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityContact extends BaseEntity {

	@Id
	@Column(name = "contact_id")
	private UUID contactId;

	@Column(name = "entity_id")
	private UUID entityId;

	@Column(name = "contact_name")
	private String contactName;

	private String email;

	private String phone;

}
