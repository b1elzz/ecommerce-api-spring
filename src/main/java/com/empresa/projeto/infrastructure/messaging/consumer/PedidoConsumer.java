package com.empresa.projeto.infrastructure.messaging.consumer;

import com.empresa.projeto.application.dto.PedidoNotificacaoDto;
import com.empresa.projeto.application.service.NotificacaoService;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final NotificacaoService notificacaoService;
    private final PedidoRepository pedidoRepository;
    private static final int MAX_ATTEMPTS = 3;

    @Retryable(maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 1000))
    @RabbitListener(queues = "${spring.rabbitmq.queue.pedido-concluido}")
    public void consumirMensagem(
            PedidoNotificacaoDto notificacao,
            @Header(value = "x-death", required = false) Map<String, Object> deathHeader) {

        try {
            log.info("Processando notificação do pedido: {}", notificacao.pedidoId());

            if (isMaxRetriesExceeded(deathHeader)) {
                handleMaxRetriesExceeded(notificacao);
                return;
            }

            Pedido pedido = pedidoRepository.findById(notificacao.pedidoId())
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

            notificacaoService.processarNotificacaoPedido(
                    pedido.getId(),
                    pedido.getTotal(),
                    pedido.getStatus().name(),
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNome()
            );

            log.info("Notificação processada com sucesso para o pedido {}", notificacao.pedidoId());

        } catch (Exception ex) {
            log.error("Falha ao processar pedido {} - Tentativa {}/{}",
                    notificacao.pedidoId(),
                    getCurrentAttempt(deathHeader),
                    MAX_ATTEMPTS,
                    ex);
            throw new AmqpRejectAndDontRequeueException("Erro no processamento");
        }
    }

    private boolean isMaxRetriesExceeded(Map<String, Object> deathHeader) {
        if (deathHeader == null) return false;
        Integer count = (Integer) deathHeader.get("count");
        return count != null && count >= MAX_ATTEMPTS;
    }

    private int getCurrentAttempt(Map<String, Object> deathHeader) {
        return deathHeader != null ? (Integer) deathHeader.getOrDefault("count", 0) + 1 : 1;
    }

    private void handleMaxRetriesExceeded(PedidoNotificacaoDto notificacao) {
        log.warn("Máximo de tentativas excedido para o pedido {}", notificacao.pedidoId());
        notificacaoService.registrarFalhaPermanente(notificacao);
    }
}