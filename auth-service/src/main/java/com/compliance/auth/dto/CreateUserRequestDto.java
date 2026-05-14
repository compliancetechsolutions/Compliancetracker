package com.compliance.auth.dto;

import com.compliance.common.dto.BaseDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Request DTO for creating a new user account.
 *
 * <p><b>Fix from original:</b> no validation annotations were present — any
 * string (including null, empty, or malformed values) was accepted and
 * persisted. All fields now have appropriate constraints.
 *
 * <p>Using {@code @Getter/@Setter/@NoArgsConstructor} instead of {@code @Data}
 * to avoid unintended {@code equals/hashCode} on mutable DTO fields.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequestDto extends BaseDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be 3–100 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Username may only contain letters, digits, dots, underscores, and hyphens"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 200, message = "Password must be 8–200 characters")
    private String password;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotEmpty(message = "At least one role must be assigned")
    private Set<String> roles;
}
