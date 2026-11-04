
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.dto.UsuarioRegistroDTO;
import com.sumac.sistema_ventas.dto.UsuarioRespuestaDTO;
import com.sumac.sistema_ventas.model.Rol;
import com.sumac.sistema_ventas.model.Usuario;
import com.sumac.sistema_ventas.service.RolService;
import com.sumac.sistema_ventas.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioService usuarioService,
            RolService rolService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRespuestaDTO>> listarTodos() {
        List<UsuarioRespuestaDTO> usuarios =
                usuarioService.listarTodos()
                        .stream()
                        .map(this::convertirRespuesta)
                        .toList();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioRespuestaDTO>> listarActivos() {
        List<UsuarioRespuestaDTO> usuarios =
                usuarioService.listarActivos()
                        .stream()
                        .map(this::convertirRespuesta)
                        .toList();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> buscarPorId(
            @PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(this::convertirRespuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrar(
            @Valid @RequestBody UsuarioRegistroDTO datos) {

        if (usuarioService.buscarPorCorreo(datos.getCorreo())
                .isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un usuario con ese correo.");
        }

        var resultadoRol = rolService.buscarPorId(datos.getRolId());

        if (resultadoRol.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("El rol indicado no existe.");
        }

        Rol rol = resultadoRol.get();

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre());
        usuario.setCorreo(datos.getCorreo());
        usuario.setContrasena(
                passwordEncoder.encode(datos.getContrasena()));
        usuario.setActivo(true);
        usuario.setRol(rol);

        Usuario guardado = usuarioService.guardar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirRespuesta(guardado));
    }

    private UsuarioRespuestaDTO convertirRespuesta(Usuario usuario) {
        String nombreRol = usuario.getRol() != null
                ? usuario.getRol().getNombre()
                : null;

        return new UsuarioRespuestaDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getActivo(),
                nombreRol
        );
    }
}
