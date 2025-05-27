package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.service.PagamentoService;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/pagamentos")
@RequiredArgsConstructor
public class WebhookController {

    private final PedidoRepository pedidoRepository;
    private final PagamentoService pagamentoService;

    @PostMapping("/mock")
    public void receberWebhookMock(@RequestBody Map<String, String> payload) {
        Long pedidoId = Long.parseLong(payload.get("pedido_id"));
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow();

        pagamentoService.processarPagamento(pedido);
    }
}