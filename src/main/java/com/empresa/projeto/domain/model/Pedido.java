package com.empresa.projeto.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "Pedido", description = "Entidade que representa um pedido de cliente")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Schema(description = "ID único do pedido", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Cliente que fez o pedido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario cliente;

    @Schema(description = "Itens do pedido")
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @Schema(description = "Valor total do pedido", example = "199.90")
    @Column(nullable = false)
    private BigDecimal total;

    @Schema(description = "Data de criação do pedido", example = "2024-01-01T10:00:00")
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Schema(description = "Status atual do pedido", example = "CRIADO")
    @Enumerated(EnumType.STRING)
    private Status status;


    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        this.itens.add(item);
    }

    @Schema(description = "Status possíveis para um pedido")
    public enum Status {
        @Schema(description = "Pedido criado mas não pago") CRIADO,
        @Schema(description = "Pedido pago com sucesso") PAGO,
        @Schema(description = "Pedido confirmado pelo sistema") CONFIRMADO,
        @Schema(description = "Pedido enviado para entrega") ENVIADO,
        @Schema(description = "Pedido entregue ao cliente") ENTREGUE,
        @Schema(description = "Pedido cancelado") CANCELADO,
        @Schema(description = "Falha na notificação do pedido") FALHA_NOTIFICACAO
    }
}