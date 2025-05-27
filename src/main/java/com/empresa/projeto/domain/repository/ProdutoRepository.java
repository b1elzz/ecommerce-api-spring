package com.empresa.projeto.domain.repository;

import com.empresa.projeto.domain.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT p FROM Produto p JOIN FETCH p.categorias WHERE p.id = :id")
    Optional<Produto> findByIdComCategorias(@Param("id") Long id);

    @Query("SELECT p FROM Produto p JOIN p.categorias c WHERE c.id = :categoriaId")
    Page<Produto> findByCategoriaId(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Modifying
    @Query("UPDATE Produto p SET p.estoque = p.estoque - :quantidade WHERE p.id = :id AND p.estoque >= :quantidade")
    int debitarEstoque(@Param("id") Long id, @Param("quantidade") Integer quantidade);

    @Query("SELECT p FROM Produto p WHERE p.estoque < :estoqueMinimo")
    List<Produto> findComEstoqueAbaixoDe(@Param("estoqueMinimo") Integer estoqueMinimo);

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Query("SELECT p FROM Produto p JOIN FETCH p.categorias")
    List<Produto> findAllComCategorias();
}