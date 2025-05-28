package com.empresa.projeto.application.dto;

import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.model.Usuario;
import java.math.BigDecimal;

public record PedidoNotificacaoDto(
        Long pedidoId,
        BigDecimal total,
        String status,
        String clienteEmail,
        String clienteNome
) {
    public static PedidoNotificacaoDto from(Pedido pedido) {
        Usuario cliente = pedido.getCliente();
        return new PedidoNotificacaoDto(
                pedido.getId(),
                pedido.getTotal(),
                pedido.getStatus().name(),
                cliente != null ? cliente.getEmail() : null,
                cliente != null ? cliente.getNome() : null
        );
    }
}