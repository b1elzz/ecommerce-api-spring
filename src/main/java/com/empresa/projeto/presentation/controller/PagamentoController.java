package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.exception.PedidoNaoEncontradoException;
import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.repository.PedidoRepository;
import com.empresa.projeto.application.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Simulação e processamento de pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PedidoRepository pedidoRepository;

    @Operation(
            summary = "Simular pagamento",
            description = "Simula o processamento de pagamento para um pedido (20% de chance de falha)",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Pagamento em processamento"),
            @ApiResponse(responseCode = "400", description = "ID do pedido inválido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PostMapping("/simular/{pedidoId}")
    public ResponseEntity<Void> simularPagamento(
            @Parameter(description = "ID do pedido", example = "1", required = true)
            @PathVariable @Positive Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));

        pagamentoService.processarPagamento(pedido);
        return ResponseEntity.accepted().build();
    }
}