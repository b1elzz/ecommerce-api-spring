package com.empresa.projeto.presentation.advice;

import com.empresa.projeto.application.exception.ErroResponse;
import com.empresa.projeto.application.exception.EstoqueInsuficienteException;
import com.empresa.projeto.application.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResponse handleEstoqueInsuficiente(EstoqueInsuficienteException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    private ErroResponse construirErroResponse(HttpStatus status, String message, HttpServletRequest request) {
        return new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }
}