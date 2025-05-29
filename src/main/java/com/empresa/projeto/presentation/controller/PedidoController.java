package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.request.PedidoRequest;
import com.empresa.projeto.application.dto.response.PedidoResponse;
import com.empresa.projeto.application.service.PedidoService;
import com.empresa.projeto.domain.model.Pedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestão de pedidos de clientes")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(
            summary = "Criar pedido",
            description = "Cria um novo pedido com os produtos selecionados",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody @Valid PedidoRequest request) {
        return pedidoService.criar(request);
    }

    @Operation(
            summary = "Listar pedidos por cliente",
            description = "Retorna todos os pedidos de um cliente específico",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem bem-sucedida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cliente/{clienteId}")
    public List<PedidoResponse> listarPorCliente(@PathVariable Long clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    @Operation(
            summary = "Buscar pedido por ID",
            description = "Retorna um pedido específico com seus itens",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @Operation(
            summary = "Atualizar status do pedido",
            description = "Atualiza o status de um pedido (ex: PAGO, CANCELADO)",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado",
                    content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(
            @PathVariable Long id,
            @RequestParam @Valid Pedido.Status status) {
        return pedidoService.atualizarStatus(id, status);
    }
}