//package com.empresa.projeto.presentation.controller;
//
//import com.empresa.projeto.application.service.PagamentoService;
//import com.empresa.projeto.domain.model.Pedido;
//import com.empresa.projeto.domain.repository.PedidoRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/webhooks/pagamentos")
//@RequiredArgsConstructor
//public class WebhookController {
//
//    private final PedidoRepository pedidoRepository;
//    private final PagamentoService pagamentoService;
//
//    @PostMapping("/mock")
//    public ResponseEntity<String> receberWebhookMock(@RequestBody Map<String, String> payload) {
//        try {
//            Long pedidoId = Long.parseLong(payload.get("pedido_id"));
//            Pedido pedido = pedidoRepository.findById(pedidoId)
//                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
//
//            pagamentoService.processarPagamento(pedido);
//            return ResponseEntity.ok("Pagamento processado para o pedido " + pedidoId);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Erro no processamento");
//        }
//    }
//}