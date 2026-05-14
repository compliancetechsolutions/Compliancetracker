package com.compliance.entity.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntityRequest {

    @NotBlank(message = "Entity name is required")
    @Size(
        min = 2,
        max = 200,
        message = "Entity name must be 2–200 characters"
    )
    private String entityName;

    private UUID entityTypeId;

    @Size(
        max = 100,
        message = "Registration number must not exceed 100 characters"
    )
    @Pattern(
        regexp = "^[A-Za-z0-9\\-/]*$",
        message = "Registration number may only contain letters, digits, hyphens, and slashes"
    )
    private String registrationNumber;
}