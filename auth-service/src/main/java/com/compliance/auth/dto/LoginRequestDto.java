package com.compliance.auth.dto;

import com.compliance.common.dto.BaseDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto extends BaseDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be 3–100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 200, message = "Password must be 8–200 characters")
    private String password;
}
