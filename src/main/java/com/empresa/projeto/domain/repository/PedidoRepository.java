package com.empresa.projeto.domain.repository;

import com.empresa.projeto.domain.model.Pedido;
import com.empresa.projeto.domain.model.Pedido.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(Status status);

    @Modifying
    @Query("UPDATE Pedido p SET p.status = :status WHERE p.id = :pedidoId")
    void atualizarStatus(@Param("pedidoId") Long pedidoId,
                         @Param("status") Status status);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdComItens(@Param("id") Long id);
}