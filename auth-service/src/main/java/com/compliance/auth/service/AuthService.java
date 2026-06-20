package com.compliance.auth.service;

import com.compliance.auth.dto.LoginRequestDto;
import com.compliance.auth.dto.LoginResponseDto;

public interface AuthService {
  LoginResponseDto login(LoginRequestDto request, String ip);
  LoginResponseDto refresh(String refreshToken);
  void logout(String refreshToken);

}



