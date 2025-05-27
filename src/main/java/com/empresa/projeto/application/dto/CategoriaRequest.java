package com.empresa.projeto.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank
        @Size(max = 100)
        String nome
) {}