package com.compliance.auth.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.compliance.auth.entity.Role;
import com.compliance.auth.entity.User;
import com.compliance.auth.entity.UserRole;
import com.compliance.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepo.findByUsernameWithRoles(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		System.out.println("USER FOUND = " + user.getUsername());
		System.out.println("DB HASH = " + user.getPasswordHash());

		log.debug("Loaded user details for username={}", user.getUsername());

		Set<GrantedAuthority> authorities = new HashSet<>();

		for (UserRole ur : user.getUserRoles()) {

			Role role = ur.getRole();

			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

			role.getRolePermissions()
					.forEach(rp -> authorities.add(new SimpleGrantedAuthority(rp.getPermission().getPermissionName())));

		}

		return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
				.password(user.getPasswordHash()).authorities(authorities).accountExpired(false).accountLocked(false)
				.credentialsExpired(false).disabled(false).build();
	}
}