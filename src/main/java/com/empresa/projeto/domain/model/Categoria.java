package com.empresa.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Schema(name = "Categoria", description = "Entidade que representa uma categoria de produtos")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categorias")
public class Categoria {

    @Schema(description = "ID único da categoria", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome da categoria (único)", example = "Eletrônicos", required = true)
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @JsonIgnore
    @Schema(description = "Produtos associados a esta categoria (não retornado na API)")
    @ManyToMany(mappedBy = "categorias")
    @Builder.Default
    private Set<Produto> produtos = new HashSet<>();


    public void adicionarProduto(Produto produto) {
        this.produtos.add(produto);
        produto.getCategorias().add(this);
    }

    public void removerProduto(Produto produto) {
        this.produtos.remove(produto);
        produto.getCategorias().remove(this);
    }
}