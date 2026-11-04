
package com.sumac.sistema_ventas.repository;

import com.sumac.sistema_ventas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByRolNombre(String nombreRol);

    boolean existsByCorreo(String correo);
}
