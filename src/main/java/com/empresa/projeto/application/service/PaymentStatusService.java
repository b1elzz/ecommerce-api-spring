package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import com.empresa.projeto.infrastructure.messaging.producer.PedidoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusService {

    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    @Transactional
    public void atualizarStatusPagamento(Long pedidoId, boolean pagamentoAprovado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (pagamentoAprovado) {
            pedido.setStatus(Pedido.Status.PAGO);
            log.info("Payment approved for order {}", pedidoId);
        } else {
            pedido.setStatus(Pedido.Status.CANCELADO);
            log.warn("Payment rejected for order {}", pedidoId);
        }

        pedidoRepository.save(pedido);

        PedidoNotificacaoDto notificacaoDto = PedidoNotificacaoDto.from(pedido);
        pedidoProducer.notificarPedidoConcluido(notificacaoDto);
    }
}