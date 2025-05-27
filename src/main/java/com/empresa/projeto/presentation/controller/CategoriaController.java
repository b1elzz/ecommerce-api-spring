package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.CategoriaRequest;
import com.empresa.projeto.application.dto.CategoriaResponse;
import com.empresa.projeto.application.service.CategoriaService;
import com.empresa.projeto.domain.model.Categoria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse criar(@RequestBody @Valid CategoriaRequest request) {
        return toResponse(categoriaService.criar(request.nome()));
    }

    @GetMapping
    public Page<CategoriaResponse> listarTodas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        List<CategoriaResponse> categorias = categoriaService.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(categorias, pageable, categorias.size());
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(@PathVariable Long id) {
        return toResponse(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaRequest request) {
        return toResponse(categoriaService.atualizar(id, request.nome()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getProdutos().stream()
                        .map(p -> new CategoriaResponse.ProdutoSimplesResponse(p.getId(), p.getNome()))
                        .collect(Collectors.toSet())
        );
    }
}