package com.empresa.projeto.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponse(
        Long id,
        String nome,
        BigDecimal preco,
        Integer estoque,
        List<CategoriaResponse> categorias
) {
    public record CategoriaResponse(Long id, String nome) {}
}