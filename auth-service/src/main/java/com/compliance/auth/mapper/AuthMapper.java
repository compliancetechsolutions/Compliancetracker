package com.compliance.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compliance.auth.dto.LoginResponseDto;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "expiresIn", ignore = true)
    LoginResponseDto toLoginResponse(String accessToken, String refreshToken);
}