package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.PedidoRequest;
import com.empresa.projeto.application.dto.PedidoResponse;
import com.empresa.projeto.domain.model.*;
import com.empresa.projeto.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Usuario cliente = usuarioRepository.findById(request.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(Pedido.Status.CRIADO);
        pedido.setItens(new ArrayList<>());

        pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            if (produto.getEstoque() < itemRequest.quantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente");
            }

            produto.setEstoque(produto.getEstoque() - itemRequest.quantidade());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.quantidade());
            item.setPrecoUnitario(produto.getPreco());

            itemPedidoRepository.save(item);
            pedido.getItens().add(item);
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(itemRequest.quantidade())));
        }

        pedido.setTotal(total);
        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, Pedido.Status status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        pedido.setStatus(status);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return toResponse(pedidoAtualizado);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getNome(),
                pedido.getDataCriacao(),
                pedido.getTotal(),
                pedido.getStatus().name(),
                pedido.getItens().stream()
                        .map(item -> new PedidoResponse.ItemPedidoResponse(
                                item.getProduto().getNome(),
                                item.getQuantidade(),
                                item.getPrecoUnitario(),
                                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
                        ))
                        .toList()
        );
    }

}