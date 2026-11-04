
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByActivoTrue();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByStockLessThanEqual(Integer stock);

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT p FROM Producto p
                WHERE p.activo = true
                AND p.stock <= p.stockMinimo
            """)
    List<Producto> findProductosActivosConStockBajo();
}