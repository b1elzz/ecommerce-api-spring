package com.empresa.projeto.application.service;

import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.model.Pedido.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class PagamentoService {

    @Transactional
    public void processarPagamento(Pedido pedido) {
        try {

            Thread.sleep(2000);

            boolean pagamentoAprovado = Math.random() > 0.2;

            if (pagamentoAprovado) {
                pedido.setStatus(Status.PAGO);
                log.info("Pagamento aprovado para pedido {}", pedido.getId());
            } else {
                pedido.setStatus(Status.CANCELADO);
                log.warn("Pagamento recusado para pedido {}", pedido.getId());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pedido.setStatus(Status.CANCELADO);
            log.error("Falha no processamento do pagamento", e);
        }
    }
}