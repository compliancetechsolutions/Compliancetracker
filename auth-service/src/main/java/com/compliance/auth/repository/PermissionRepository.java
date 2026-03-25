package com.compliance.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByPermissionName(String permissionName);
}