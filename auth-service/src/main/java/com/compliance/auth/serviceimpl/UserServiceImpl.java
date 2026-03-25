package com.compliance.auth.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;
import com.compliance.auth.entity.Role;
import com.compliance.auth.entity.User;
import com.compliance.auth.entity.UserRole;
import com.compliance.auth.mapper.UserMapper;
import com.compliance.auth.repository.RoleRepository;
import com.compliance.auth.repository.UserRepository;
import com.compliance.auth.repository.UserRoleRepository;
import com.compliance.auth.service.UserService;
import com.compliance.common.exception.UnauthorizedException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional

public class UserServiceImpl implements UserService {

	 private final UserRepository userRepo;
	    private final RoleRepository roleRepo;
	    private final UserRoleRepository userRoleRepo;
	    private final UserMapper userMapper;
	    private final PasswordEncoder passwordEncoder;

	    // ✅ CREATE USER
	    @Override
	    public UserResponseDto createUser(CreateUserRequestDto request) {

	        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
	            throw new RuntimeException("Username already exists");
	        }

	        if (request.getRoles() == null || request.getRoles().isEmpty()) {
	            throw new UnauthorizedException("User must have at least one role");
	        }

	        User user = User.builder()
	                .userId(UUID.randomUUID())
	                .username(request.getUsername())
	                .email(request.getEmail())
	                .passwordHash(passwordEncoder.encode(request.getPassword()))
	                .status("ACTIVE")
	                .build();

	        userRepo.save(user);

	        assignRolesToUser(user.getUserId(), new ArrayList<>(request.getRoles()));

	        return userMapper.toDto(user);
	    }

	    // ✅ GET ALL USERS
	    @Override
	    public List<UserResponseDto> getAllUsers() {
	        return userRepo.findAll()
	                .stream()
	                .map(userMapper::toDto)
	                .toList();
	    }

	    // ✅ GET BY ID
	    @Override
	    public UserResponseDto getUserById(UUID userId) {
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UnauthorizedException("User not found"));
	        return userMapper.toDto(user);
	    }

	    // ✅ GET BY USERNAME
	    @Override
	    public UserResponseDto getUserByUsername(String username) {
	        User user = userRepo.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        return userMapper.toDto(user);
	    }

	    // ✅ UPDATE USER
	    @Override
	    public UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request) {

	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (request.getEmail() != null)
	            user.setEmail(request.getEmail());

	        if (request.getStatus() != null)
	            user.setStatus(request.getStatus());

	        userRepo.save(user);

	        if (request.getRoles() != null) {
	            userRoleRepo.deleteByUser_UserId(userId);
	            assignRolesToUser(userId, new ArrayList<>(request.getRoles()));
	        }

	        return userMapper.toDto(user);
	    }

	    // ✅ DELETE USER
	    @Override
	    public void deleteUser(UUID userId) {

	        if (!userRepo.existsById(userId)) {
	            throw new RuntimeException("User not found");
	        }

	        userRoleRepo.deleteByUser_UserId(userId);
	        userRepo.deleteById(userId);
	    }

	    // ✅ ASSIGN SINGLE ROLE
	    @Override
	    public void assignRoleToUser(UUID userId, String roleName) {

	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        Role role = roleRepo.findByRoleName(roleName)
	                .orElseThrow(() -> new RuntimeException("Role not found"));

	        // 🔥 prevent duplicate role
	        boolean alreadyAssigned = userRoleRepo.findByUser_UserId(userId)
	                .stream()
	                .anyMatch(r -> r.getRole().getRoleName().equals(roleName));

	        if (alreadyAssigned) return;

	        UserRole userRole = new UserRole();
	        userRole.setUserRoleId(UUID.randomUUID());
	        userRole.setUser(user);
	        userRole.setRole(role);
	        userRole.setAssignedAt(LocalDateTime.now());

	        userRoleRepo.save(userRole);
	    }

	    // ✅ REMOVE ROLE
	    @Override
	    public void removeRoleFromUser(UUID userId, String roleName) {

	        List<UserRole> roles = userRoleRepo.findByUser_UserId(userId);

	        roles.stream()
	                .filter(r -> r.getRole().getRoleName().equals(roleName))
	                .forEach(userRoleRepo::delete);
	    }

	    // ✅ ASSIGN MULTIPLE ROLES
	    @Override
	    public void assignRolesToUser(UUID userId, List<String> roles) {

	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        List<Role> roleEntities = roleRepo.findByRoleNameIn(roles);

	        if (roleEntities.isEmpty()) {
	            throw new RuntimeException("Roles not found");
	        }

	        List<UserRole> userRoles = roleEntities.stream().map(role -> {

	            UserRole ur = new UserRole();
	            ur.setUserRoleId(UUID.randomUUID());
	            ur.setUser(user);
	            ur.setRole(role);
	            ur.setAssignedAt(LocalDateTime.now());
	            return ur;

	        }).toList();

	        userRoleRepo.saveAll(userRoles);
	    }

	    // ✅ GET USER ROLES
	    @Override
	    public List<String> getUserRoles(UUID userId) {

	        return userRoleRepo.findByUser_UserId(userId)
	                .stream()
	                .map(ur -> ur.getRole().getRoleName())
	                .toList();
	    }
	    }














