package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.request.AuthLoginRequest;
import com.empresa.projeto.application.dto.request.AuthRegisterRequest;
import com.empresa.projeto.application.dto.response.AuthResponse;
import com.empresa.projeto.application.exception.UsuarioJaExisteException;
import com.empresa.projeto.domain.model.Usuario;
import com.empresa.projeto.domain.repository.UsuarioRepository;
import com.empresa.projeto.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(AuthRegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new UsuarioJaExisteException(request.email());
        }

        var usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Usuario.Role.USER)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRole().name());
    }

    public AuthResponse autenticar(AuthLoginRequest request) {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );


        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new AuthResponse(
                jwtService.generateToken(usuario),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}