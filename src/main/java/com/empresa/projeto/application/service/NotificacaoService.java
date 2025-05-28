package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final JavaMailSender mailSender;
    private final PedidoRepository pedidoRepository;

    @Async
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void processarNotificacaoPedido(Long pedidoId,
                                           BigDecimal total,
                                           String status,
                                           String clienteEmail,
                                           String clienteNome) {

        try {
            log.info("Iniciando envio de notificação para pedido {} - Cliente: {}", pedidoId, clienteEmail);
            log.debug("Configuração SMTP: Host={}, Port={}",
                    mailSender instanceof JavaMailSenderImpl ?
                            ((JavaMailSenderImpl) mailSender).getHost() : "n/a",
                    mailSender instanceof JavaMailSenderImpl ?
                            ((JavaMailSenderImpl) mailSender).getPort() : 0);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@empresa.com");
            message.setTo(clienteEmail);
            message.setSubject("🛍️ Status do Pedido #" + pedidoId);
            message.setText(buildEmailContent(clienteNome, pedidoId, total, status));

            mailSender.send(message);
            log.info("Notificação enviada com sucesso para {}", clienteEmail);

        } catch (Exception ex) {
            log.error("FALHA CRÍTICA no envio para pedido {} - Erro: {}", pedidoId, ex.getMessage());
            log.debug("Stacktrace completo:", ex);
            throw new NotificationException("Falha no envio de notificação", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarFalhaPermanente(PedidoNotificacaoDto notificacao) {
        log.warn("Registrando falha permanente para pedido {}", notificacao.pedidoId());
        pedidoRepository.atualizarStatus(notificacao.pedidoId(), Pedido.Status.FALHA_NOTIFICACAO);


        log.debug("Status atualizado para FALHA_NOTIFICACAO no pedido {}", notificacao.pedidoId());
    }

    private String buildEmailContent(String clienteNome, Long pedidoId, BigDecimal total, String status) {
        return String.format(
                """
                Olá %s,
                
                O status do seu pedido #%d foi atualizado:
                
                Valor Total: R$ %.2f
                Status: %s
                
                Atenciosamente,
                Equipe de Vendas""",
                clienteNome, pedidoId, total, status
        );
    }

    public static class NotificationException extends RuntimeException {
        public NotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}