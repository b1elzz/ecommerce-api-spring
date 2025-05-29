    package com.empresa.projeto.presentation.controller;

    import com.empresa.projeto.application.dto.request.AuthLoginRequest;
    import com.empresa.projeto.application.dto.request.AuthRegisterRequest;
    import com.empresa.projeto.application.dto.response.AuthResponse;
    import com.empresa.projeto.application.service.AuthService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Tag(name = "Autenticação", description = "Operações de login e registro")
    public class AuthController {

        private final AuthService authService;

        @Operation(summary = "Registrar usuário", description = "Cria um novo usuário com role USER")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                @ApiResponse(responseCode = "409", description = "Email já cadastrado")
        })
        @PostMapping("/registrar")
        public ResponseEntity<AuthResponse> registrar(@RequestBody @Valid AuthRegisterRequest request) {
            return ResponseEntity.ok(authService.registrar(request));
        }

        @Operation(summary = "Login", description = "Autentica usuário e retorna token JWT")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        })
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest request) {
            return ResponseEntity.ok(authService.autenticar(request));
        }
    }