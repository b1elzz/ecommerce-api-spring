package com.empresa.projeto.infrastructure.messaging.consumer;

import com.empresa.projeto.domain.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoConsumer {

    @RabbitListener(queues = "${spring.rabbitmq.queue.pedido-concluido}")
    public void consumirMensagem(Pedido pedido) {
        try {
            log.info("Processando notificação do pedido: {}", pedido.getId());
            //Futura implementação
        } catch (Exception e) {
            log.error("Falha ao processar pedido {}", pedido.getId(), e);
        }
    }
}