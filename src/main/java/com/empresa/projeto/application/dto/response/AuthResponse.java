package com.empresa.projeto.application.dto.response;

public record AuthResponse(
        String token,
        String email,
        String role
) {}