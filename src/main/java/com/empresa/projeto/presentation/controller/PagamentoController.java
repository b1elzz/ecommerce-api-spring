package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.exception.PedidoNaoEncontradoException;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import com.empresa.projeto.application.service.PagamentoService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PedidoRepository pedidoRepository;

    @PostMapping("/simular/{pedidoId}")
    public ResponseEntity<Void> simularPagamento(
            @PathVariable @Positive Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));

        pagamentoService.processarPagamento(pedido);
        return ResponseEntity.accepted().build();
    }
}