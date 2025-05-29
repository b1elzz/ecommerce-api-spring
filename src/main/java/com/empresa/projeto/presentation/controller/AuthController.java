package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.request.AuthLoginRequest;
import com.empresa.projeto.application.dto.request.AuthRegisterRequest;
import com.empresa.projeto.application.dto.response.AuthResponse;
import com.empresa.projeto.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário com role USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário registrado"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrar(@RequestBody @Valid AuthRegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest request) {
        return ResponseEntity.ok(authService.autenticar(request));
    }
}