package com.empresa.projeto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank String nome,
        @Positive BigDecimal preco,
        @PositiveOrZero Integer estoque
) {}