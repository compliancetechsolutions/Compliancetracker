package com.compliance.auth.entity;

import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role_permissions", schema = "auth_schema")
@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "role_permission_id")
	private UUID rolePermissionId;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "permission_id")
	private Permission permission;

}
