package com.compliance.auth.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compliance.auth.dto.UserResponseDto;
import com.compliance.auth.entity.User;

@Mapper(componentModel = "spring")

public interface UserMapper {
	
	  @Mapping(target = "roles", expression = "java(mapRoles(user))")
	    UserResponseDto toDto(User user);

	    default Set<String> mapRoles(User user) {
	        if (user.getUserRoles() == null) return Set.of();

	        return user.getUserRoles()
	                .stream()
	                .map(ur -> ur.getRole().getRoleName())
	                .collect(Collectors.toSet());
	    }

}
