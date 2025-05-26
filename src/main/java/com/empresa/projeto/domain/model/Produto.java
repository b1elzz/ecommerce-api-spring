package com.empresa.projeto.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itens;
}