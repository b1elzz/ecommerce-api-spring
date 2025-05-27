package com.empresa.projeto.application.mapper;

import com.empresa.projeto.application.dto.PedidoRequest;
import com.empresa.projeto.application.dto.PedidoResponse;
import com.empresa.projeto.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "itens", source = "itens", qualifiedByName = "mapItens")
    @Mapping(target = "status", expression = "java(pedido.getStatus().name())")
    PedidoResponse toResponse(Pedido pedido);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "itens", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "status", constant = "CRIADO")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    Pedido toEntity(PedidoRequest request);

    @Named("mapItens")
    default List<PedidoResponse.ItemPedidoResponse> mapItens(List<ItemPedido> itens) {
        return itens.stream()
                .map(this::mapItem)
                .toList();
    }

    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "subtotal", expression = "java(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))")
    PedidoResponse.ItemPedidoResponse mapItem(ItemPedido item);
}