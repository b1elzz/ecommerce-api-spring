package com.empresa.projeto.presentation.controller;

import com.empresa.projeto.application.dto.request.ProdutoRequest;
import com.empresa.projeto.application.dto.response.ProdutoResponse;
import com.empresa.projeto.application.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Produtos", description = "Gestão de produtos do catálogo")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(
            summary = "Criar produto",
            description = "Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> criar(@RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.criar(request));
    }

    @Operation(
            summary = "Listar produtos",
            description = "Retorna todos os produtos com paginação")
    @ApiResponse(
            responseCode = "200",
            description = "Listagem bem-sucedida",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(produtoService.listarTodos(pageable));
    }

    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna um produto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(
            summary = "Listar produtos por categoria",
            description = "Filtra produtos por ID de categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem filtrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProdutoResponse>> buscarPorCategoria(
            @Parameter(description = "ID da categoria", example = "2", required = true)
            @PathVariable Long categoriaId,
            Pageable pageable) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoriaId, pageable));
    }

    @Operation(
            summary = "Atualizar produto",
            description = "Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado",
                    content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> atualizar(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @Operation(
            summary = "Excluir produto",
            description = "Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar produtos com estoque crítico",
            description = "Retorna produtos com estoque abaixo do nível mínimo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem bem-sucedida"),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido")
    })
    @GetMapping("/estoque-critico")
    public ResponseEntity<List<ProdutoResponse>> listarComEstoqueCritico(
            @Parameter(description = "Nível mínimo de estoque", example = "5")
            @RequestParam(defaultValue = "5") Integer estoqueMinimo) {
        return ResponseEntity.ok(produtoService.listarComEstoqueCritico(estoqueMinimo));
    }

    @Operation(
            summary = "Adicionar categorias ao produto",
            description = "Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias vinculadas",
                    content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "IDs inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto ou categoria não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}/categorias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> adicionarCategorias(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody List<Long> categoriaIds) {
        return ResponseEntity.ok(produtoService.adicionarCategorias(id, categoriaIds));
    }
}