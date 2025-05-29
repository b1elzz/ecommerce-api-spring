//package com.empresa.projeto.presentation.controller;
//
//import com.empresa.projeto.application.service.NotificacaoService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigDecimal;
//
//@RestController
//@RequestMapping("/teste-email-html")
//public class TesteEmailHtmlController {
//
//    private final NotificacaoService notificacaoService;
//
//    public TesteEmailHtmlController(NotificacaoService notificacaoService) {
//        this.notificacaoService = notificacaoService;
//    }
//
//    @GetMapping
//    public String testarEmailHtml() {
//        notificacaoService.processarNotificacaoPedido(
//                999L,
//                new BigDecimal("199.90"),
//                "PAGO",
//                "email_de_teste@dominio.com",
//                "Cliente Teste"
//        );
//        return "E-mail HTML disparado!";
//    }
//}