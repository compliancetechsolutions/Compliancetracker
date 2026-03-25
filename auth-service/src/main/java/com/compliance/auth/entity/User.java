package com.compliance.auth.entity;

import java.util.Set;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users", schema = "auth_schema")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class User extends BaseEntity {
	@Id
	@Column(name = "user_id")
	private UUID userId;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	private String status;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<UserRole> userRoles;
}
