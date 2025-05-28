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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "produtos")
public class ProdutoService {

    private static final String CACHE_ALL = "'all'";
    private static final String CACHE_CATEGORIA_PREFIX = "'categoria:'";

    private final ProdutoRepository repository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = CACHE_ALL),
            @CacheEvict(key = CACHE_CATEGORIA_PREFIX + "+ #result.categorias.![id]")
    })
    public ProdutoResponse criar(ProdutoRequest request) {
        validarDadosProduto(request);

        Produto produto = Produto.builder()
                .nome(request.nome())
                .preco(request.preco())
                .estoque(request.estoque())
                .build();

        Produto produtoSalvo = repository.save(produto);
        log.info("Produto criado: ID {}", produtoSalvo.getId());
        return toResponse(produtoSalvo);
    }

    @Cacheable(key = CACHE_ALL)
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarTodos(Pageable pageable) {
        log.debug("Consultando todos os produtos (paginado)");
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Cacheable(key = CACHE_CATEGORIA_PREFIX + "+ #categoriaId")
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        log.debug("Consultando produtos da categoria ID: {}", categoriaId);
        return repository.findByCategoriaId(categoriaId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = CACHE_ALL),
            @CacheEvict(key = CACHE_CATEGORIA_PREFIX + "+ #result.categorias.![id]")
    })
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        validarDadosProduto(request);

        Produto produto = repository.findByIdComCategorias(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());

        log.info("Atualizando produto ID: {}", id);
        return toResponse(repository.save(produto));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = CACHE_ALL),
            @CacheEvict(key = CACHE_CATEGORIA_PREFIX + "+ T(java.util.Collections).singletonList(#id)")
    })
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ProdutoNaoEncontradoException(id);
        }
        log.info("Removendo produto ID: {}", id);
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarComEstoqueCritico(Integer estoqueMinimo) {
        log.debug("Consultando produtos com estoque abaixo de {}", estoqueMinimo);
        return repository.findComEstoqueAbaixoDe(estoqueMinimo).stream()
                .map(this::toResponse)
                .toList();
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