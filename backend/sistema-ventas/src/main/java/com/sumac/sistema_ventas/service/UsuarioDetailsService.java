
package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.Usuario;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    public UsuarioDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioService.buscarPorCorreo(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No existe un usuario con ese correo"));

        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException(
                    "El usuario no tiene un rol asignado");
        }

        String nombreRol = usuario.getRol().getNombre();

        if (!nombreRol.startsWith("ROLE_")) {
            nombreRol = "ROLE_" + nombreRol;
        }

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .authorities(List.of(
                        new SimpleGrantedAuthority(nombreRol)))
                .disabled(!usuario.getActivo())
                .build();
    }
}
