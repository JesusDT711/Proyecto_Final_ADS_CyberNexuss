
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstadoIgnoreCase(String estado);

    List<Pedido> findByFechaPedidoBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );
}
