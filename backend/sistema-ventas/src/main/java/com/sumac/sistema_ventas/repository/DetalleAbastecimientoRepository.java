
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.DetalleAbastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleAbastecimientoRepository extends JpaRepository<DetalleAbastecimiento, Long> {

    List<DetalleAbastecimiento> findBySolicitud_Id(Long solicitudId);

    List<DetalleAbastecimiento> findByProductoId(Long productoId);
}
