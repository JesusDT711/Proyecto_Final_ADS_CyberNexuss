
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDocumento(String documento);

    Optional<Cliente> findByCorreo(String correo);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByDocumento(String documento);
}
