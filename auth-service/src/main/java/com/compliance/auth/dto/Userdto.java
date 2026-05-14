package com.compliance.auth.dto;

import java.util.Set;

import com.compliance.common.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false) // 🔥 FIX
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Userdto extends BaseDto { // 🔥 FIX NAME

    private String username;
    private String email;
    private Set<String> roles;
}