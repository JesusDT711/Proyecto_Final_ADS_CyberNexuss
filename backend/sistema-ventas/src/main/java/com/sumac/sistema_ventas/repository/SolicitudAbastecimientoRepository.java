
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.SolicitudAbastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudAbastecimientoRepository
        extends JpaRepository<SolicitudAbastecimiento, Long> {

    List<SolicitudAbastecimiento> findByProveedorId(Long proveedorId);

    List<SolicitudAbastecimiento> findByUsuarioId(Long usuarioId);

    List<SolicitudAbastecimiento> findByEstadoIgnoreCase(String estado);
}

