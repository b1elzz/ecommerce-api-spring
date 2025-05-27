package com.empresa.projeto.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String PEDIDO_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDO_QUEUE = "pedidos.concluido";
    public static final String PEDIDO_DLQ = "pedidos.concluido.dlq";
    public static final String PEDIDO_ROUTING_KEY = "pedido.concluido";

    @Bean
    public DirectExchange pedidoExchange() {
        return new DirectExchange(PEDIDO_EXCHANGE, true, false);
    }

    @Bean
    public Queue pedidoQueue() {
        return QueueBuilder.durable(PEDIDO_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PEDIDO_DLQ)
                .build();
    }

    @Bean
    public Queue pedidoDLQ() {
        return QueueBuilder.durable(PEDIDO_DLQ).build();
    }

    @Bean
    public Binding pedidoBinding() {
        return BindingBuilder.bind(pedidoQueue())
                .to(pedidoExchange())
                .with(PEDIDO_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        rabbitTemplate.setRetryTemplate(retryTemplate);
        rabbitTemplate.setReplyTimeout(60000);

        return rabbitTemplate;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "", PEDIDO_DLQ);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(10);
        return factory;
    }


}