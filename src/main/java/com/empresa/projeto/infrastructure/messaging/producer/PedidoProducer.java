package com.empresa.projeto.infrastructure.messaging.producer;

import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.infrastructure.messaging.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Retryable(value = AmqpException.class, maxAttempts = MAX_RETRY_ATTEMPTS)
    public void notificarPedidoConcluido(PedidoNotificacaoDto notificacaoDto) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PEDIDO_EXCHANGE,
                    RabbitMQConfig.PEDIDO_ROUTING_KEY,
                    notificacaoDto,
                    message -> {
                        message.getMessageProperties().setContentType("application/json");
                        message.getMessageProperties().setCorrelationId(notificacaoDto.pedidoId().toString());
                        return message;
                    }
            );
        } catch (AmqpException ex) {
            log.error("Falha crítica ao enviar pedido {} para a fila principal", notificacaoDto.pedidoId());
            enviarParaDLQ(notificacaoDto, "Erro na fila principal: " + ex.getMessage());
            throw ex;
        }
    }

    private void enviarParaDLQ(PedidoNotificacaoDto notificacaoDto, String motivo) {
        try {
            rabbitTemplate.convertAndSend(
                    "",
                    RabbitMQConfig.PEDIDO_DLQ,
                    notificacaoDto,
                    message -> {
                        message.getMessageProperties()
                                .setHeader("X-Failure-Reason", motivo);
                        return message;
                    }
            );
        } catch (Exception ex) {
            log.error("Falha CRÍTICA ao enviar pedido {} para DLQ", notificacaoDto.pedidoId(), ex);
        }
    }
}