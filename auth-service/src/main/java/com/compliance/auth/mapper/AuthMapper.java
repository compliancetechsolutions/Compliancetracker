package com.compliance.auth.mapper;

import org.mapstruct.Mapper;

import com.compliance.auth.dto.LoginResponseDto;

@Mapper(componentModel = "spring")
public interface AuthMapper {
	
	LoginResponseDto toLoginResponse(String accessToken, String refreshToken);
	

}
