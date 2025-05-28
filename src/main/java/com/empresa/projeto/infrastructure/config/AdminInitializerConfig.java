package com.empresa.projeto.infrastructure.config;

import com.empresa.projeto.domain.model.Usuario;
import com.empresa.projeto.domain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializerConfig {

    @Bean
    CommandLineRunner initAdminUser(UsuarioRepository usuarioRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nome("Admin Master")
                        .email("admin@empresa.com")
                        .senha(passwordEncoder.encode("Admin123@"))
                        .role(Usuario.Role.ADMIN)
                        .build();

                usuarioRepository.save(admin);
                System.out.println("Usuário admin criado com sucesso!");
            }
        };
    }
}