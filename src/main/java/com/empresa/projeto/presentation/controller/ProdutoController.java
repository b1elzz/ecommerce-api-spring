package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.ProdutoRequest;
import com.empresa.projeto.application.dto.ProdutoResponse;
import com.empresa.projeto.application.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> criar(@RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(produtoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProdutoResponse>> buscarPorCategoria(
            @PathVariable Long categoriaId,
            Pageable pageable) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoriaId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estoque-critico")
    public ResponseEntity<List<ProdutoResponse>> listarComEstoqueCritico(
            @RequestParam(defaultValue = "5") Integer estoqueMinimo) {
        return ResponseEntity.ok(produtoService.listarComEstoqueCritico(estoqueMinimo));
    }


    @PutMapping("/{id}/categorias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> adicionarCategorias(
            @PathVariable Long id,
            @RequestBody List<Long> categoriaIds) {
        return ResponseEntity.ok(produtoService.adicionarCategorias(id, categoriaIds));
    }
}