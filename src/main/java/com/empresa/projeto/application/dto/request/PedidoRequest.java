package com.empresa.projeto.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record PedidoRequest(
        @NotNull @Positive Long clienteId,
        @NotNull List<ItemPedidoRequest> itens
) {
    public record ItemPedidoRequest(
            @NotNull @Positive Long produtoId,
            @NotNull @Positive Integer quantidade
    ) {}
}