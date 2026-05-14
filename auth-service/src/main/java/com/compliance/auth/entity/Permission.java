package com.compliance.auth.entity;

import java.util.Set;
import java.util.UUID;

import com.compliance.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions", schema = "auth_schema")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "permission_id")
    private UUID permissionId;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;

    @JsonIgnore
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private Set<RolePermission> rolePermissions;
}