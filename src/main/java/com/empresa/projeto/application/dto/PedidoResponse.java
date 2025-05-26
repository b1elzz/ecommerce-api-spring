package com.empresa.projeto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String clienteNome,
        LocalDateTime dataCriacao,
        BigDecimal total,
        String status,
        List<ItemPedidoResponse> itens
) {
    public record ItemPedidoResponse(
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}
}