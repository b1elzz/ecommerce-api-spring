package com.empresa.projeto.infrastructure.messaging.consumer;

import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.application.service.NotificacaoService;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
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
            @Header(value = "x-death", required = false) Map<String, Object> deathHeader,
            @Header(AmqpHeaders.CORRELATION_ID) String correlationId) {

        try {
            log.info("Processando pedido (CorrelationID: {}): {}", correlationId, notificacao.pedidoId());

            if (isMaxRetriesExceeded(deathHeader)) {
                handleMaxRetriesExceeded(notificacao, correlationId);
                return;
            }

            Pedido pedido = pedidoRepository.findByIdComCliente(notificacao.pedidoId())
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

            notificacaoService.processarNotificacaoPedido(
                    pedido.getId(),
                    pedido.getTotal(),
                    pedido.getStatus().name(),
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNome()
            );

        } catch (Exception ex) {
            log.error("Falha ao processar pedido {} (CorrelationID: {}) - Tentativa {}/{} - Erro: {}",
                    notificacao.pedidoId(),
                    correlationId,
                    getCurrentAttempt(deathHeader),
                    MAX_ATTEMPTS,
                    ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Erro no processamento");
        }
    }


    private boolean isMaxRetriesExceeded(Map<String, Object> deathHeader) {
        if (deathHeader == null) return false;
        Integer count = (Integer) ((List<Map<String, Object>>) deathHeader.get("count")).get(0).get("count");
        return count != null && count >= MAX_ATTEMPTS;
    }

    private int getCurrentAttempt(Map<String, Object> deathHeader) {
        if (deathHeader == null) return 1;
        Integer count = (Integer) ((List<Map<String, Object>>) deathHeader.get("count")).get(0).get("count");
        return (count != null) ? count + 1 : 1;
    }

    private void handleMaxRetriesExceeded(PedidoNotificacaoDto notificacao, String correlationId) {
        log.warn("Máximo de tentativas excedido para pedido {} (CorrelationID: {})",
                notificacao.pedidoId(), correlationId);
        notificacaoService.registrarFalhaPermanente(notificacao);
    }
}