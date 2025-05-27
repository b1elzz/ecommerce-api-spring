package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.CategoriaRequest;
import com.empresa.projeto.application.dto.CategoriaResponse;
import com.empresa.projeto.application.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse criar(@RequestBody @Valid CategoriaRequest request) {
        return categoriaService.criar(request);
    }

    @GetMapping
    public Page<CategoriaResponse> listarTodas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return categoriaService.listarTodas(pageable);
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaRequest request) {
        return categoriaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
    }
}