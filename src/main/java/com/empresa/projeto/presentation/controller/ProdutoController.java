package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.ProdutoRequest;
import com.empresa.projeto.application.dto.ProdutoResponse;
import com.empresa.projeto.application.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> criarProduto(@RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.criar(request));
    }
}