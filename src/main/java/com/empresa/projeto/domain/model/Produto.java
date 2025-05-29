package com.empresa.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(name = "Produto", description = "Entidade que representa um produto no catálogo")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produto")
public class Produto {

    @Schema(description = "ID único do produto", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do produto", example = "Smartphone Premium", required = true)
    @Column(nullable = false)
    private String nome;

    @Schema(description = "Preço do produto com duas casas decimais", example = "999.99", required = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Schema(description = "Quantidade em estoque", example = "10", required = true)
    @Column(nullable = false)
    private Integer estoque;

    @JsonIgnore
    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itens;

    @Schema(description = "Categorias associadas ao produto")
    @ManyToMany
    @JoinTable(
            name = "produto_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();


    public void addCategoria(Categoria categoria) {
        this.categorias.add(categoria);
        categoria.getProdutos().add(this);
    }
}