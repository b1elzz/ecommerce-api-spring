package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.PedidoRequest;
import com.empresa.projeto.application.dto.PedidoResponse;
import com.empresa.projeto.application.service.PedidoService;
import com.empresa.projeto.domain.model.Pedido;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody @Valid PedidoRequest request) {
        return pedidoService.criar(request);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<PedidoResponse> listarPorCliente(@PathVariable Long clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(
            @PathVariable Long id,
            @RequestParam @Valid Pedido.Status status) {
        return pedidoService.atualizarStatus(id, status);
    }
}