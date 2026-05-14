package com.compliance.auth.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class LoginResponseDto {

    private String       accessToken;
    private String       refreshToken;
    private UUID         userId;
    private String       username;
    private List<String> roles;
    private long         expiresIn;   // seconds until access token expires
}


