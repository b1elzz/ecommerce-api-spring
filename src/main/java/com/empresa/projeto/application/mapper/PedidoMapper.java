package com.empresa.projeto.application.mapper;

import com.empresa.projeto.application.dto.PedidoRequest;
import com.empresa.projeto.application.dto.PedidoResponse;
import com.empresa.projeto.domain.model.ItemPedido;
import com.empresa.projeto.domain.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "itens", source = "itens", qualifiedByName = "mapItens")
    @Mapping(target = "status", expression = "java(pedido.getStatus().name())")
    PedidoResponse toResponse(Pedido pedido);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "itens", source = ".", qualifiedByName = "novaListaVazia")
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "status", constant = "CRIADO")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    Pedido toEntity(PedidoRequest request);

    @Named("novaListaVazia")
    default List<ItemPedido> novaListaVazia(PedidoRequest request) {
        return new ArrayList<>();
    }

    @Named("mapItens")
    default List<PedidoResponse.ItemPedidoResponse> mapItens(List<ItemPedido> itens) {
        if (itens == null) return new ArrayList<>();
        return itens.stream()
                .map(this::mapItem)
                .toList();
    }

    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "subtotal", expression = "java(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))")
    PedidoResponse.ItemPedidoResponse mapItem(ItemPedido item);


}
