package com.compliance.auth.entity;

import java.util.Set;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "permissions", schema = "auth_schema")
@Data
@EqualsAndHashCode(callSuper = true)

public class Permission extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name = "permission_id")
	private UUID permissionId;

	@Column(name = "permission_name", nullable = false, unique = true)
	private String permissionName;

	// 🔥 OPTIONAL but recommended (for RBAC traversal)
	@OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
	private Set<RolePermission> rolePermissions;

}
