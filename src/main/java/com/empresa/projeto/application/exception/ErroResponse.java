package com.empresa.projeto.application.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}