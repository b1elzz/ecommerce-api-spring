package com.empresa.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Schema(name = "ItemPedido", description = "Entidade que representa um item específico dentro de um pedido")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Schema(description = "ID único do item", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Schema(description = "Produto relacionado a este item")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Schema(description = "Quantidade do produto", example = "2", required = true)
    @Column(nullable = false)
    private Integer quantidade;

    @Schema(description = "Preço unitário no momento da compra", example = "99.95", required = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;


    @Schema(description = "Subtotal do item (calculado: precoUnitario * quantidade)")
    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}