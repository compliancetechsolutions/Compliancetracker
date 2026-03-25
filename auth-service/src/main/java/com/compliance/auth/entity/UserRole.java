package com.compliance.auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_roles", schema = "auth_schema")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = { "user", "role" })
public class UserRole extends BaseEntity {

	@Id
	@Column(name = "user_role_id")
	private UUID userRoleId;

	// ✅ FIX 1: USER RELATION
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// ✅ FIX 2: ROLE RELATION (THIS CREATES getRole())
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	@Column(name = "user_id")
	private UUID userId;
}
