
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioId(Long usuarioId);

    List<Reporte> findByTipoIgnoreCase(String tipo);

    List<Reporte> findByFormatoIgnoreCase(String formato);
}
