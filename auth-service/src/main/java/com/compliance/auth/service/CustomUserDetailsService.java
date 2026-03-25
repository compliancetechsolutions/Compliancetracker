package com.compliance.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.compliance.auth.entity.Permission;
import com.compliance.auth.entity.Role;
import com.compliance.auth.entity.User;
import com.compliance.auth.entity.UserRole;
import com.compliance.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // 🔥 ROLE + PERMISSION LOADING
        for (UserRole ur : user.getUserRoles()) {

            Role role = ur.getRole();
            
           

            // ROLE
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            // PERMISSIONS
            for (Permission p : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(p.getPermissionName()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }}