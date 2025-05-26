package com.empresa.projeto.application.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {}