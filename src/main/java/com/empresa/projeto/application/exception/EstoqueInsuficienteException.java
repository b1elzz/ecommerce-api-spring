package com.empresa.projeto.application.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String produtoNome) {
        super("Estoque insuficiente para o produto: " + produtoNome);
    }
}
