package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.request.PedidoRequest;
import com.empresa.projeto.application.dto.response.PedidoNotificacaoDto;
import com.empresa.projeto.application.dto.response.PedidoResponse;
import com.empresa.projeto.application.exception.*;
import com.empresa.projeto.application.mapper.PedidoMapper;
import com.empresa.projeto.domain.model.*;
import com.empresa.projeto.domain.repository.*;
import com.empresa.projeto.infrastructure.messaging.producer.PedidoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    private final CategoriaRepository categoriaRepository;

    @Transactional
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public PedidoResponse criar(PedidoRequest request) {
        try {
            log.info("Criando pedido para cliente ID: {}", request.clienteId());
            Pedido pedido = criarPedidoComItens(request);
            enviarNotificacaoAssincrona(pedido);
            return pedidoMapper.toResponse(pedido);
        } catch (EstoqueInsuficienteException ex) {
            log.error("Estoque insuficiente: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Falha ao criar pedido", ex);
            throw new PedidoException("Falha ao processar pedido", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return pedidoRepository.findByIdComItens(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, Pedido.Status novoStatus) {
        Pedido pedido = pedidoRepository.findByIdComItens(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));

        validarTransicaoStatus(pedido.getStatus(), novoStatus);
        pedidoRepository.atualizarStatus(id, novoStatus);
        pedido.setStatus(novoStatus);

        if (novoStatus == Pedido.Status.CONFIRMADO) {
            enviarNotificacaoAssincrona(pedido);
        }

        return pedidoMapper.toResponse(pedido);
    }


    private Pedido criarPedidoComItens(PedidoRequest request) {
        Pedido pedido = pedidoMapper.toEntity(request);
        pedido.setCliente(buscarCliente(request.clienteId()));

        BigDecimal total = processarItensPedido(request, pedido);
        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    private Usuario buscarCliente(Long clienteId) {
        return usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", clienteId));
    }

    private BigDecimal processarItensPedido(PedidoRequest request, Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = buscarProduto(itemRequest.produtoId());
            validarEstoque(produto, itemRequest.quantidade());

            ItemPedido item = criarItemPedido(pedido, produto, itemRequest.quantidade());
            total = total.add(calcularSubtotal(item));

            atualizarEstoqueProduto(produto, itemRequest.quantidade());
        }
        return total;
    }

    private Produto buscarProduto(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", produtoId));
    }

    private void validarEstoque(Produto produto, Integer quantidade) {
        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(produto.getNome());
        }
    }

    private ItemPedido criarItemPedido(Pedido pedido, Produto produto, Integer quantidade) {
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(produto.getPreco())
                .build();
        return itemPedidoRepository.save(item);
    }

    private void atualizarEstoqueProduto(Produto produto, Integer quantidade) {
        produto.setEstoque(produto.getEstoque() - quantidade);
        produtoRepository.save(produto);
    }

    private BigDecimal calcularSubtotal(ItemPedido item) {
        return item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }

    private void validarTransicaoStatus(Pedido.Status atual, Pedido.Status novo) {
        if (atual == Pedido.Status.CANCELADO || atual == Pedido.Status.ENTREGUE) {
            throw new IllegalStateException("Pedido finalizado não pode ser alterado");
        }
        if (novo == Pedido.Status.CRIADO) {
            throw new IllegalStateException("Transição inválida para status 'CRIADO'");
        }
    }

    private void enviarNotificacaoAssincrona(Pedido pedido) {
        try {
            PedidoNotificacaoDto notificacao = PedidoNotificacaoDto.from(pedido);
            pedidoProducer.notificarPedidoConcluido(notificacao);
        } catch (Exception ex) {
            log.error("Falha ao enfileirar notificação para pedido {}", pedido.getId(), ex);
        }
    }


}