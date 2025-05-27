package com.empresa.projeto.application.service;

import com.empresa.projeto.domain.model.Categoria;
import com.empresa.projeto.domain.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria criar(String nome) {
        if (categoriaRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        return categoriaRepository.save(
                Categoria.builder()
                        .nome(nome)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
    }

    @Transactional
    public Categoria atualizar(Long id, String novoNome) {
        Categoria categoria = buscarPorId(id);

        if (categoriaRepository.existsByNome(novoNome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoria.setNome(novoNome);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);

        if (!categoria.getProdutos().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir uma categoria com produtos vinculados");
        }

        categoriaRepository.delete(categoria);
    }
}