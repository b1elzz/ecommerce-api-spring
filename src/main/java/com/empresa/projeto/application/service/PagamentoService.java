package com.empresa.projeto.application.service;

import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.model.Pedido.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PaymentStatusService paymentStatusService;

    @Transactional
    public void processarPagamento(Pedido pedido) {
        try {

            Thread.sleep(2000);

            boolean pagamentoAprovado = Math.random() > 0.2;


            paymentStatusService.atualizarStatusPagamento(
                    pedido.getId(),
                    pagamentoAprovado
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            paymentStatusService.atualizarStatusPagamento(
                    pedido.getId(),
                    false
            );
            log.error("Payment processing interrupted", e);
        }
    }
}