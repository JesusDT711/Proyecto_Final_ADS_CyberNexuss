
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByPedidoId(Long pedidoId);

    List<Entrega> findByEstadoIgnoreCase(String estado);
}
