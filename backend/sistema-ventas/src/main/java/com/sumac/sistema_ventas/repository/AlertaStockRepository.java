package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.AlertaStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {

    List<AlertaStock> findByAtendidaFalse();

    List<AlertaStock> findByProductoId(Long productoId);

    Optional<AlertaStock> findFirstByProductoIdAndAtendidaFalse(Long productoId);
}