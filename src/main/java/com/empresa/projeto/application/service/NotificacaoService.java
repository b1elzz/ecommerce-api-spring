package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final JavaMailSender mailSender;
    private final PedidoRepository pedidoRepository;
    private final TemplateEngine templateEngine;

    @Async
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void processarNotificacaoPedido(Long pedidoId,
                                           BigDecimal total,
                                           String status,
                                           String clienteEmail,
                                           String clienteNome) {
        try {
            log.info("Preparando notificação HTML para pedido {} - Cliente: {}", pedidoId, clienteEmail);


            Context context = new Context();
            context.setVariable("clienteNome", clienteNome);
            context.setVariable("pedidoId", pedidoId);
            context.setVariable("total", total);
            context.setVariable("status", status);


            String htmlContent = templateEngine.process("emails/pedido-status", context);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom("no-reply@empresa.com");
            helper.setTo(clienteEmail);
            helper.setSubject("🛍️ Status do Pedido #" + pedidoId);
            helper.setText(htmlContent, true); // true = HTML


            mailSender.send(message);
            log.info("E-mail HTML enviado com sucesso para {}", clienteEmail);

        } catch (MessagingException ex) {
            log.error("Falha ao enviar e-mail HTML para pedido {} - Erro: {}", pedidoId, ex.getMessage());
            throw new NotificationException("Falha no envio de notificação HTML", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao processar notificação para pedido {}", pedidoId, ex);
            throw new NotificationException("Erro inesperado no serviço de notificação", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarFalhaPermanente(PedidoNotificacaoDto notificacao) {
        log.warn("Registrando falha permanente para pedido {}", notificacao.pedidoId());
        pedidoRepository.atualizarStatus(notificacao.pedidoId(), Pedido.Status.FALHA_NOTIFICACAO);
        log.debug("Status atualizado para FALHA_NOTIFICACAO no pedido {}", notificacao.pedidoId());
    }


    private void enviarEmailTextoSimples(String clienteEmail, Long pedidoId,
                                         BigDecimal total, String status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@empresa.com");
            message.setTo(clienteEmail);
            message.setSubject("Status do Pedido #" + pedidoId);
            message.setText(buildEmailContent(clienteEmail, pedidoId, total, status));
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Fallback para e-mail texto também falhou", ex);
        }
    }

    private String buildEmailContent(String clienteNome, Long pedidoId,
                                     BigDecimal total, String status) {
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