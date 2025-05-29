//package com.empresa.projeto.presentation.controller;
//
//import com.empresa.projeto.application.dto.request.CategoriaRequest;
//import com.empresa.projeto.application.dto.response.CategoriaResponse;
//import com.empresa.projeto.application.service.CategoriaService;
//import com.empresa.projeto.domain.model.Categoria;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/categorias")
//@RequiredArgsConstructor
//@Tag(name = "Categorias", description = "Gestão de categorias de produtos")
//public class CategoriaController {
//
//    private final CategoriaService categoriaService;
//
//    @Operation(
//            summary = "Criar categoria",
//            description = "Acesso exclusivo para ADMIN",
//            security = @SecurityRequirement(name = "JWT"))
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
//                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
//            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
//            @ApiResponse(responseCode = "403", description = "Acesso negado")
//    })
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('ADMIN')")
//    public CategoriaResponse criar(@RequestBody @Valid CategoriaRequest request) {
//        return toResponse(categoriaService.criar(request.nome()));
//    }
//
//    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias paginadas")
//    @ApiResponse(responseCode = "200", description = "Listagem bem-sucedida")
//    @GetMapping
//    public Page<CategoriaResponse> listarTodas(
//            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
//        List<CategoriaResponse> categorias = categoriaService.listarTodas().stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//        return new PageImpl<>(categorias, pageable, categorias.size());
//    }
//
//    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Categoria encontrada",
//                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
//            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
//    })
//    @GetMapping("/{id}")
//    public CategoriaResponse buscarPorId(@PathVariable Long id) {
//        return toResponse(categoriaService.buscarPorId(id));
//    }
//
//    @Operation(
//            summary = "Atualizar categoria",
//            description = "Acesso exclusivo para ADMIN",
//            security = @SecurityRequirement(name = "JWT"))
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Categoria atualizada",
//                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
//            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
//            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
//            @ApiResponse(responseCode = "403", description = "Acesso negado")
//    })
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public CategoriaResponse atualizar(
//            @PathVariable Long id,
//            @RequestBody @Valid CategoriaRequest request) {
//        return toResponse(categoriaService.atualizar(id, request.nome()));
//    }
//
//    @Operation(
//            summary = "Excluir categoria",
//            description = "Acesso exclusivo para ADMIN",
//            security = @SecurityRequirement(name = "JWT"))
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Categoria excluída"),
//            @ApiResponse(responseCode = "400", description = "Não é possível excluir categoria com produtos"),
//            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
//            @ApiResponse(responseCode = "403", description = "Acesso negado")
//    })
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
//    public void excluir(@PathVariable Long id) {
//        categoriaService.excluir(id);
//    }
//
//    private CategoriaResponse toResponse(Categoria categoria) {
//        return new CategoriaResponse(
//                categoria.getId(),
//                categoria.getNome(),
//                categoria.getProdutos().stream()
//                        .map(p -> new CategoriaResponse.ProdutoSimplesResponse(p.getId(), p.getNome()))
//                        .collect(Collectors.toSet())
//        );
//    }
//}