package com.compliance.auth.entity;

import java.util.Set;
import java.util.UUID;

import com.compliance.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "auth_schema")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Role extends BaseEntity {

    @Id
    @Column(name = "role_id")
    @EqualsAndHashCode.Include
    private UUID roleId;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<UserRole> userRoles;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<RolePermission> rolePermissions;
}