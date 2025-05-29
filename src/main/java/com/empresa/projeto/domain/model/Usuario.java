package com.empresa.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Schema(name = "Usuario", description = "Entidade que representa um usuário do sistema")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Schema(description = "ID único do usuário", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "João Silva", required = true)
    @Column(nullable = false, length = 100)
    private String nome;

    @Schema(description = "Email do usuário (utilizado como login)", example = "joao@empresa.com", required = true)
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String senha;

    @Schema(description = "Perfil de acesso do usuário", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
    @Enumerated(EnumType.STRING)
    private Role role;



    @Schema(hidden = true)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Schema(hidden = true)
    @Override
    public String getPassword() {
        return senha;
    }

    @Schema(hidden = true)
    @Override
    public String getUsername() {
        return email;
    }

    @Schema(hidden = true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Schema(name = "Role", description = "Perfis de acesso disponíveis")
    public enum Role implements GrantedAuthority {
        @Schema(description = "Usuário comum") USER,
        @Schema(description = "Administrador do sistema") ADMIN;

        @Override
        public String getAuthority() {
            return "ROLE_" + name();
        }
    }
}