package com.compliance.auth.service;



import com.compliance.auth.dto.LoginResponseDto;

public interface AuthService {
	LoginResponseDto login(String username, String password, String ip);
	void logout(String refreshToken);
	LoginResponseDto refresh(String refreshToken);

}
