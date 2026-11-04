
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.SolicitudAbastecimiento;
import com.sumac.sistema_ventas.service.SolicitudAbastecimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-abastecimiento")
public class SolicitudAbastecimientoController {

    private final SolicitudAbastecimientoService solicitudService;

    public SolicitudAbastecimientoController(
            SolicitudAbastecimientoService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudAbastecimiento>> listarTodos() {
        return ResponseEntity.ok(solicitudService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudAbastecimiento> buscarPorId(
            @PathVariable Long id) {
        return solicitudService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<SolicitudAbastecimiento>> listarPorProveedor(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(
                solicitudService.listarPorProveedor(proveedorId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitudAbastecimiento>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                solicitudService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudAbastecimiento>> listarPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                solicitudService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<SolicitudAbastecimiento> crear(
            @RequestBody SolicitudAbastecimiento solicitud) {
        SolicitudAbastecimiento nuevaSolicitud = solicitudService.guardar(solicitud);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevaSolicitud);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudAbastecimiento> actualizar(
            @PathVariable Long id,
            @RequestBody SolicitudAbastecimiento solicitud) {

        return solicitudService.buscarPorId(id)
                .map(existente -> {
                    solicitud.setId(id);
                    SolicitudAbastecimiento actualizado = solicitudService.guardar(solicitud);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (solicitudService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recibir")
    public ResponseEntity<SolicitudAbastecimiento> recibir(
            @PathVariable Long id) {

        SolicitudAbastecimiento solicitudRecibida = solicitudService.recibirSolicitud(id);

        return ResponseEntity.ok(solicitudRecibida);
    }

}
