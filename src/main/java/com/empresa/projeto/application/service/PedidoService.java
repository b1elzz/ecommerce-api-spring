package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.PedidoRequest;
import com.empresa.projeto.application.dto.PedidoResponse;
import com.empresa.projeto.application.exception.EstoqueInsuficienteException;
import com.empresa.projeto.application.exception.PedidoNaoEncontradoException;
import com.empresa.projeto.application.exception.RecursoNaoEncontradoException;
import com.empresa.projeto.application.mapper.PedidoMapper;
import com.empresa.projeto.domain.model.*;
import com.empresa.projeto.domain.repository.*;
import com.empresa.projeto.infrastructure.messaging.producer.PedidoProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final PedidoProducer pedidoProducer;


    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Pedido pedido = pedidoMapper.toEntity(request);
        pedido.setCliente(usuarioRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", request.clienteId())));

        pedidoRepository.save(pedido);
        pedidoProducer.notificarPedidoConcluido(pedido);
        processarItensPedido(request, pedido);

        return pedidoMapper.toResponse(pedidoRepository.save(pedido));

    }

    private void processarItensPedido(PedidoRequest request, Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", itemRequest.produtoId()));

            validarEstoque(produto, itemRequest.quantidade());
            atualizarEstoque(produto, itemRequest.quantidade());

            ItemPedido item = criarItemPedido(pedido, produto, itemRequest.quantidade());
            total = total.add(calcularSubtotal(item));
        }

        pedido.setTotal(total);
    }

    private ItemPedido criarItemPedido(Pedido pedido, Produto produto, Integer quantidade) {
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(produto.getPreco())
                .build();

        itemPedidoRepository.save(item);
        pedido.getItens().add(item);
        return item;
    }

    private void validarEstoque(Produto produto, Integer quantidade) {
        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(produto.getNome());
        }
    }

    private void atualizarEstoque(Produto produto, Integer quantidade) {
        produto.setEstoque(produto.getEstoque() - quantidade);
        produtoRepository.save(produto);
    }

    private BigDecimal calcularSubtotal(ItemPedido item) {
        return item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, Pedido.Status status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));

        pedido.setStatus(status);
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }
}