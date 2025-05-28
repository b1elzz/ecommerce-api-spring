package com.empresa.projeto.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha
) {}