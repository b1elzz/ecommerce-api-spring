package com.empresa.projeto.application.exception;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(Long id) {
        super("Pedido com ID " + id + " não encontrado");
    }
}