package com.compliance.auth.entity;

import java.util.Set;
import java.util.UUID;

import com.compliance.entity.BaseEntity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles", schema = "auth_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
	 @Id
	    @Column(name = "role_id")
	    private UUID roleId;

	    @Column(name = "role_name", unique = true, nullable = false)
	    private String roleName;

	    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
	    private Set<UserRole> userRoles;

	    @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(
	        name = "role_permissions",
	        schema = "auth_schema",
	        joinColumns = @JoinColumn(name = "role_id"),
	        inverseJoinColumns = @JoinColumn(name = "permission_id")
	    )
	    private Set<Permission> permissions;}











