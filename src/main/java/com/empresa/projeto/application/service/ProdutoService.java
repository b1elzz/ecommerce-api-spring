package com.empresa.projeto.application.service;

import com.empresa.projeto.application.dto.ProdutoRequest;
import com.empresa.projeto.application.dto.ProdutoResponse;
import com.empresa.projeto.domain.model.Produto;
import com.empresa.projeto.domain.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository repository;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = Produto.builder()
                .nome(request.nome())
                .preco(request.preco())
                .estoque(request.estoque())
                .build();

        return toResponse(repository.save(produto));
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
        );
    }

    public List<ProdutoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProdutoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }
}