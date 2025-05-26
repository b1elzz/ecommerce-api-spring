package com.empresa.projeto.application.exception;

public class UsuarioJaExisteException extends RuntimeException {
    public UsuarioJaExisteException(String email) {
        super("O email " + email + " já está cadastrado.");
    }
}