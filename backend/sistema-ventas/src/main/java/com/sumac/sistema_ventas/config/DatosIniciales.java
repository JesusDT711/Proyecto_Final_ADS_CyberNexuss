package com.sumac.sistema_ventas.config;

import com.sumac.sistema_ventas.model.Rol;
import com.sumac.sistema_ventas.model.Usuario;
import com.sumac.sistema_ventas.service.RolService;
import com.sumac.sistema_ventas.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Al arrancar, crea los roles y el administrador inicial si todavía no existen.
 * Reemplaza la importación manual del SQL.
 * Acceso inicial: admin@sumac.local / Admin12345  (cámbiala después).
 */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatosIniciales(
            RolService rolService,
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Rol admin = rolService.buscarPorNombre("ROLE_ADMIN")
                    .orElseGet(() -> rolService.guardar(new Rol("ROLE_ADMIN")));
            rolService.buscarPorNombre("ROLE_EMPLEADO")
                    .orElseGet(() -> rolService.guardar(new Rol("ROLE_EMPLEADO")));

            if (usuarioService.buscarPorCorreo("admin@sumac.local").isEmpty()) {
                Usuario u = new Usuario();
                u.setNombre("Administrador Inicial");
                u.setCorreo("admin@sumac.local");
                u.setContrasena(passwordEncoder.encode("Admin12345"));
                u.setActivo(true);
                u.setRol(admin);
                usuarioService.guardar(u);
                System.out.println(">>> Administrador creado: admin@sumac.local / Admin12345");
            }
        };
    }
}
