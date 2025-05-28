package com.empresa.projeto.application.dto.response;

import java.util.Set;

public record CategoriaResponse(
        Long id,
        String nome,
        Set<ProdutoSimplesResponse> produtos
) {
    public record ProdutoSimplesResponse(
            Long id,
            String nome
    ) {}
}