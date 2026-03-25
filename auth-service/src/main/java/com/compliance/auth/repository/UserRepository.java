package com.compliance.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);

}
