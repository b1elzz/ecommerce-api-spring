package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.service.PagamentoService;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PedidoRepository pedidoRepository;

    @PostMapping("/simular/{pedidoId}")
    public ResponseEntity<Void> simularPagamento(@PathVariable Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        pagamentoService.processarPagamento(pedido);
        return ResponseEntity.accepted().build();
    }
}