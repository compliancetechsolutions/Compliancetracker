package com.compliance.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {


	List<Role> findByRoleNameIn(List<String> roleNames);
	 Optional<Role> findByRoleNameIgnoreCase(String roleName);
}