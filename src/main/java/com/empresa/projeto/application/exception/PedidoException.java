package com.empresa.projeto.application.exception;

public class PedidoException extends RuntimeException {
    public PedidoException(String message, Throwable cause) {
        super(message, cause);
    }
}