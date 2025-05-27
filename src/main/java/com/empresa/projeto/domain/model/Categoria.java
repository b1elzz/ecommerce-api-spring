package com.empresa.projeto.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

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