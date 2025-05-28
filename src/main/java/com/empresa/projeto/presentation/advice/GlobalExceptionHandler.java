package com.empresa.projeto.presentation.advice;

import com.empresa.projeto.application.exception.ErroResponse;
import com.empresa.projeto.application.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return construirErroResponse(HttpStatus.BAD_REQUEST, mensagem, request);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String mensagem = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        return construirErroResponse(HttpStatus.BAD_REQUEST, mensagem, request);
    }


    @ExceptionHandler({
            RecursoNaoEncontradoException.class,
            PedidoNaoEncontradoException.class,
            ProdutoNaoEncontradoException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse handleRecursoNaoEncontrado(RuntimeException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({
            EstoqueInsuficienteException.class,
            EstoqueNegativoException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResponse handleEstoqueExceptions(RuntimeException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse handleUsuarioJaExiste(UsuarioJaExisteException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }


    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErroResponse handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResponse handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return construirErroResponse(HttpStatus.FORBIDDEN, "Acesso negado", request);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensagem = String.format("Parâmetro '%s' com valor '%s' é inválido. Tipo esperado: %s",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "");

        return construirErroResponse(HttpStatus.BAD_REQUEST, mensagem, request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String mensagem = "Ocorreu um erro interno no servidor";

        if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            mensagem = ex.getMessage();
        }

        return ResponseEntity.status(status)
                .body(construirErroResponse(status, mensagem, request));
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