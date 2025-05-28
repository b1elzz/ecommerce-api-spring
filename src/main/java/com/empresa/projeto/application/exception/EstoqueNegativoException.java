package com.empresa.projeto.application.exception;

public class EstoqueNegativoException extends RuntimeException {
    public EstoqueNegativoException(String produtoNome) {
        super("Estoque não pode ser negativo para o produto: " + produtoNome);
    }
}