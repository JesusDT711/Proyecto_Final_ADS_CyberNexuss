
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductoId(Long productoId);

    List<MovimientoInventario> findByUsuarioId(Long usuarioId);

    List<MovimientoInventario> findByTipoMovimientoIgnoreCase(
            String tipoMovimiento
    );

    List<MovimientoInventario> findByFechaMovimientoBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );
}
