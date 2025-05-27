package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.ProdutoRequest;
import com.empresa.projeto.application.dto.ProdutoResponse;
import com.empresa.projeto.application.exception.EstoqueNegativoException;
import com.empresa.projeto.application.exception.ProdutoNaoEncontradoException;
import com.empresa.projeto.domain.model.Produto;
import com.empresa.projeto.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "produtos")
public class ProdutoService {

    private final ProdutoRepository repository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "'categoria:' + #result.categoriaId")
    })
    public ProdutoResponse criar(ProdutoRequest request) {
        validarDadosProduto(request);

        Produto produto = Produto.builder()
                .nome(request.nome())
                .preco(request.preco())
                .estoque(request.estoque())
                .build();

        return toResponse(repository.save(produto));
    }

    @Cacheable(key = "'all'")
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarTodos(Pageable pageable) {
        log.debug("Buscando todos os produtos no banco de dados");
        return repository.findAll(pageable)
                .map(this::toResponse);
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        log.debug("Buscando produto por ID: {}", id);
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Cacheable(key = "'categoria:' + #categoriaId")
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        return repository.findByCategoriasId(categoriaId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "'categoria:' + #result.categoriaId")
    })
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        validarDadosProduto(request);

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());

        return toResponse(repository.save(produto));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "'categoria:' + #result.categoriaId")
    })
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ProdutoNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategorias().stream()
                        .map(c -> new ProdutoResponse.CategoriaResponse(c.getId(), c.getNome()))
                        .toList()
        );
    }

    private void validarDadosProduto(ProdutoRequest request) {
        if (request.estoque() < 0) {
            throw new EstoqueNegativoException(request.nome());
        }
        if (request.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
    }
}