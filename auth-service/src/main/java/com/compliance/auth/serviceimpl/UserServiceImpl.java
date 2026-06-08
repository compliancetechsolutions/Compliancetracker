package com.compliance.auth.serviceimpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.auth.dto.BulkCreateUserRequestDto;
import com.compliance.auth.dto.BulkUserCreateResponseDto;
import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.RoleAssignmentResultDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;
import com.compliance.auth.entity.Role;
import com.compliance.auth.entity.User;
import com.compliance.auth.entity.UserRole;
import com.compliance.auth.event.UserEventProducer;
import com.compliance.auth.mapper.UserMapper;
import com.compliance.auth.repository.RoleRepository;
import com.compliance.auth.repository.UserRepository;
import com.compliance.auth.repository.UserRoleRepository;
import com.compliance.auth.service.UserService;
import com.compliance.common.enums.AuthErrorCode;
import com.compliance.common.enums.UserErrorCode;
import com.compliance.common.exception.BaseException;
import com.compliance.common.exception.ResourceNotFoundException;
import com.compliance.common.exception.UserAlreadyExistsException;
import com.compliance.common.kafka.event.UserEvent;
import com.compliance.common.kafka.event.UserEventType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final UserRoleRepository userRoleRepo;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserEventProducer userEventProducer;

	// =========================================================
	// CREATE
	// =========================================================
	@Override
	public UserResponseDto create(CreateUserRequestDto request) {

		log.info("CREATE USER API HIT");

		if (userRepo.existsByUsernameIgnoreCase(request.getUsername())) {
			throw new UserAlreadyExistsException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		if (userRepo.existsByEmailIgnoreCase(request.getEmail())) {
			throw new UserAlreadyExistsException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		if (request.getRoles() == null || request.getRoles().isEmpty()) {
			throw new BaseException(UserErrorCode.USER_MUST_HAVE_ROLE);
		}

		User user = User.builder().username(request.getUsername().trim().toLowerCase())
				.email(request.getEmail().trim().toLowerCase())
				.passwordHash(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName())
				.lastName(request.getLastName()).status("ACTIVE").build();

		User savedUser = userRepo.saveAndFlush(user);

		assignRolesToUser(savedUser.getUserId(), new ArrayList<>(request.getRoles()));

		userRepo.flush();

		User reloaded = userRepo.findByIdWithRoles(savedUser.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, savedUser.getUserId()));

		// =====================================
		// PUBLISH USER EVENT
		// =====================================

		UserEvent event = UserEvent.builder()

				.eventId(UUID.randomUUID())

				.createdAt(LocalDateTime.now())

				.eventType("USER_CREATED")

				.userId(reloaded.getUserId())

				.username(reloaded.getUsername())

				.email(reloaded.getEmail())

				.status(reloaded.getStatus())

				.roles(

						reloaded.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleName())
								.collect(Collectors.toSet()))

				.build();

		userEventProducer.publishUserCreatedEvent(event.getEventId(), event);

		return userMapper.toDto(reloaded);

	}

	// =========================================================
	// UPDATE
	// =========================================================
	@Override
	public UserResponseDto update(UUID userId, UpdateUserRequestDto request) {

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId));

		UserEvent event = UserEvent.builder().eventId(UUID.randomUUID()).eventType(UserEventType.UPDATE_USER)
				.aggregateId(user.getUserId()).serviceName("auth-service").userId(user.getUserId())
				.username(user.getUsername()).email(user.getEmail()).performedBy("SYSTEM")
				.actionTime(LocalDateTime.now()).build();

		userEventProducer.publish(event);

		log.info("Publishing UPDATE_USER event: {}", event);

		return updateUserFields(user, request);
	}

	// =========================================================
	// GET BY ID
	// =========================================================
	@Override
	@Transactional(readOnly = true)
	public Optional<UserResponseDto> getById(UUID userId) {

		return Optional.of(userMapper.toDto(userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId))));
	}

	// =========================================================
	// GET ALL
	// =========================================================
	@Override
	@Transactional(readOnly = true)
	public Page<UserResponseDto> getAll(Pageable pageable) {

		return userRepo.findAll(pageable).map(userMapper::toDto);
	}

	// =========================================================
	// DELETE (SOFT DELETE)
	// =========================================================
	@Override
	public void delete(UUID userId) {

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId));

		user.setIsDeleted(true);
		user.setStatus("INACTIVE");

		userRepo.save(user);

		// =====================================
		// PUBLISH EVENT
		// =====================================

		UserEvent event = UserEvent.builder().eventId(UUID.randomUUID()).createdAt(LocalDateTime.now())

				.eventType("USER_DELETED")

				.userId(user.getUserId()).username(user.getUsername()).email(user.getEmail()).status(user.getStatus())

				.roles(user.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleName())
						.collect(Collectors.toSet()))
				.build();

		userEventProducer.publish(event);

	}

	// =========================================================
	// EXTRA METHODS
	// =========================================================
	@Override
	@Transactional(readOnly = true)
	public UserResponseDto getUserByUsername(String username) {

		return userMapper.toDto(userRepo.findByUsernameIgnoreCase(username.trim())
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, username)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getUserRoles(UUID userId) {
		userRepo.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException(AuthErrorCode.USER_NOT_FOUND, "User ID not found: " + userId));

		return userRoleRepo.findByUser_UserId(userId).stream().map(ur -> ur.getRole().getRoleName()).toList();
	}

	@Override
	public void assignRoleToUser(UUID userId, String roleName) {

		assignRolesToUser(userId, List.of(roleName));
	}

	@Override
	public RoleAssignmentResultDto assignRolesToUser(UUID userId, List<String> roleNames) {

		User user = userRepo.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResourceNotFoundException(AuthErrorCode.USER_NOT_FOUND, userId));

		Set<String> requestedRoles = roleNames.stream().filter(Objects::nonNull).map(String::trim)
				.map(String::toUpperCase).collect(Collectors.toSet());

		Set<String> existingRoles = user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName().toUpperCase())
				.collect(Collectors.toSet());

		List<String> added = new ArrayList<>();
		List<String> skipped = new ArrayList<>();
		List<String> notFound = new ArrayList<>();

		for (String roleName : requestedRoles) {

			if (existingRoles.contains(roleName)) {
				skipped.add(roleName);
				continue;
			}

			Optional<Role> roleOpt = roleRepo.findByRoleNameIgnoreCase(roleName);
			if (roleOpt.isEmpty()) {
				throw new ResourceNotFoundException(UserErrorCode.ROLE_NOT_FOUND, "Role not found: " + roleName);
			}
			Role role = roleOpt.get();

			UserRole userRole = UserRole.builder().userRoleId(UUID.randomUUID()).user(user).role(role).build();

			user.getUserRoles().add(userRole);

			added.add(roleName);
		}

		userRepo.save(user);

		// =====================================
		// PUBLISH USER ROLE UPDATED EVENT
		// =====================================

		User reloaded = userRepo.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId));

		UserEvent event = UserEvent.builder().eventId(UUID.randomUUID()).createdAt(LocalDateTime.now())

				.eventType("USER_ROLE_UPDATED")

				.userId(reloaded.getUserId()).username(reloaded.getUsername()).email(reloaded.getEmail())
				.status(reloaded.getStatus())

				.roles(reloaded.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleName())
						.collect(Collectors.toSet()))
				.build();

		userEventProducer.publish(event);

		return new RoleAssignmentResultDto(added, skipped, notFound);
	}

	@Override
	public void removeRoleFromUser(UUID userId, String roleName) {

		User user = userRepo.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId));

		boolean removed = user.getUserRoles()
				.removeIf(ur -> ur.getRole() != null && ur.getRole().getRoleName().equalsIgnoreCase(roleName));

		if (!removed) {
			throw new ResourceNotFoundException(UserErrorCode.ROLE_NOT_FOUND, roleName);
		}

		userRepo.save(user);
	}

	private void updateEmailIfNeeded(User user, UpdateUserRequestDto request) {

		String email = request.getEmail();

		if (email == null || email.isBlank()) {
			return;
		}

		String newEmail = email.trim().toLowerCase();

		if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepo.existsByEmailIgnoreCase(newEmail)) {

			throw new UserAlreadyExistsException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		user.setEmail(newEmail);
	}

	private UserResponseDto updateUserFields(User user, UpdateUserRequestDto request) {

		updateIfPresent(request.getFirstName(), user::setFirstName);
		updateIfPresent(request.getLastName(), user::setLastName);
		updateEmailIfNeeded(user, request);
		updateIfPresent(request.getStatus(), user::setStatus);
		updatePasswordIfPresent(user, request.getPassword());
		userRepo.save(user);
		return userMapper.toDto(user);
	}

	private void updatePasswordIfPresent(User user, String password) {

		if (password != null && !password.isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(password));
		}
	}

	private void updateIfPresent(String value, Consumer<String> setter) {

		if (value != null && !value.isBlank()) {
			setter.accept(value.trim());
		}
	}

	@Override
	@Transactional
	public BulkUserCreateResponseDto createBulkUsers(BulkCreateUserRequestDto request) {

		List<UserResponseDto> createdUsers = new ArrayList<>();

		List<String> skippedUsers = new ArrayList<>();

		for (CreateUserRequestDto dto : request.getUsers()) {

			try {

				boolean usernameExists = userRepo.existsByUsernameIgnoreCase(dto.getUsername());
				boolean emailExists = userRepo.existsByEmailIgnoreCase(dto.getEmail());
				if (usernameExists || emailExists) {
					skippedUsers.add(dto.getUsername());
					continue;
				}

				User user = new User();
				user.setUsername(dto.getUsername());
				user.setEmail(dto.getEmail());
				user.setFirstName(dto.getFirstName());
				user.setLastName(dto.getLastName());
				user.setStatus("ACTIVE");
				user.setIsDeleted(false);
				user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
				User savedUser = userRepo.save(user);
				// ROLE ASSIGNMENT
				if (dto.getRoles() != null) {
					assignRolesToUser(savedUser.getUserId(), new ArrayList<>(dto.getRoles()));
				}

				// =========================
				// PUBLISH EVENT
				// =========================

				UserEvent event = UserEvent.builder().eventType("USER_CREATED").userId(savedUser.getUserId())
						.username(savedUser.getUsername()).email(savedUser.getEmail())
						.firstName(savedUser.getFirstName()).lastName(savedUser.getLastName())
						.status(savedUser.getStatus()).build();

				userEventProducer.publish(event);

				createdUsers.add(userMapper.toDto(savedUser));
			} catch (Exception ex) {

				ex.printStackTrace();
				skippedUsers.add(dto.getUsername());
			}
		}

		return BulkUserCreateResponseDto.builder().message(createdUsers.size() + " users created successfully")
				.totalRequested(request.getUsers().size()).createdCount(createdUsers.size())
				.skippedCount(skippedUsers.size()).createdUsers(createdUsers).skippedUsers(skippedUsers).build();
	}
}