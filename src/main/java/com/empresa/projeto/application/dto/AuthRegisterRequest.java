package com.empresa.projeto.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha
) {}