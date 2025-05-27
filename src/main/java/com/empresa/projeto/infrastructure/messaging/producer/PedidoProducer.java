package com.empresa.projeto.infrastructure.messaging.producer;

import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.infrastructure.messaging.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void notificarPedidoConcluido(Pedido pedido) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PEDIDO_CONCLUIDO_QUEUE,
                    pedido
            );
            log.info("Notificação enviada para o pedido: {}", pedido.getId());
        } catch (Exception e) {
            log.error("Falha ao enviar notificação para o pedido {}", pedido.getId(), e);
        }
    }
}