package com.compliance.auth.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/encode")
    public String encode() {

        return passwordEncoder.encode(
                "Password@123");
    }
}