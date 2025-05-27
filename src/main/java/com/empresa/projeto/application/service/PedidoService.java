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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        log.info("Criando novo pedido para o cliente: {}", request.clienteId());


        Pedido pedido = pedidoMapper.toEntity(request);
        pedido.setCliente(usuarioRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", request.clienteId())));


        BigDecimal total = processarItensPedido(request, pedido);
        pedido.setTotal(total);


        pedido = pedidoRepository.save(pedido);
        log.info("Pedido {} criado com sucesso. Total: {}", pedido.getId(), pedido.getTotal());


        pedidoProducer.notificarPedidoConcluido(pedido);

        return pedidoMapper.toResponse(pedido);
    }

    private BigDecimal processarItensPedido(PedidoRequest request, Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        log.info("Processando {} itens para o pedido", request.itens().size());

        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> {
                        log.error("Produto não encontrado: {}", itemRequest.produtoId());
                        return new RecursoNaoEncontradoException("Produto", itemRequest.produtoId());
                    });

            validarEstoque(produto, itemRequest.quantidade());
            ItemPedido item = criarItemPedido(pedido, produto, itemRequest.quantidade());
            total = total.add(calcularSubtotal(item));


            atualizarEstoque(produto, itemRequest.quantidade());
        }

        return total;
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
        log.debug("Item criado: {} unidades do produto {}", quantidade, produto.getId());

        return item;
    }

    private void validarEstoque(Produto produto, Integer quantidade) {
        if (produto.getEstoque() < quantidade) {
            log.warn("Estoque insuficiente para o produto {} (Estoque: {}, Solicitado: {})",
                    produto.getId(), produto.getEstoque(), quantidade);
            throw new EstoqueInsuficienteException(produto.getNome());
        }
    }

    private void atualizarEstoque(Produto produto, Integer quantidade) {
        produto.setEstoque(produto.getEstoque() - quantidade);
        produtoRepository.save(produto);
        log.debug("Estoque atualizado para o produto {}. Novo estoque: {}", produto.getId(), produto.getEstoque());
    }

    private BigDecimal calcularSubtotal(ItemPedido item) {
        return item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long clienteId) {
        log.info("Listando pedidos para o cliente: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(() -> {
                    log.error("Pedido não encontrado: {}", id);
                    return new PedidoNaoEncontradoException(id);
                });
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, Pedido.Status status) {
        log.info("Atualizando status do pedido {} para {}", id, status);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pedido não encontrado para atualização: {}", id);
                    return new PedidoNaoEncontradoException(id);
                });

        pedido.setStatus(status);
        pedido = pedidoRepository.save(pedido);
        log.info("Status do pedido {} atualizado para {}", id, status);

        return pedidoMapper.toResponse(pedido);
    }
}