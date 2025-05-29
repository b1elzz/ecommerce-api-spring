package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.service.PagamentoService;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Endpoint para integração com gateways de pagamento")
public class WebhookController {

    private final PedidoRepository pedidoRepository;
    private final PagamentoService pagamentoService;

    @Operation(
            summary = "Receber notificação de pagamento",
            description = "Endpoint para receber callbacks de provedores de pagamento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"pedido_id\": \"123\", \"status\": \"approved\", \"valor\": 199.90}"
                            ),
                            schema = @Schema(implementation = Map.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento processado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject("Pagamento processado para o pedido 123")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no processamento")
    })
    @PostMapping("/mock")
    public ResponseEntity<String> receberWebhookMock(
            @RequestBody Map<String, String> payload) {
        try {
            Long pedidoId = Long.parseLong(payload.get("pedido_id"));
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

            pagamentoService.processarPagamento(pedido);
            return ResponseEntity.ok("Pagamento processado para o pedido " + pedidoId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro no processamento");
        }
    }
}