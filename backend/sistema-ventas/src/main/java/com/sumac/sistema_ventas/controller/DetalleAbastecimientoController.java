package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.DetalleAbastecimiento;
import com.sumac.sistema_ventas.service.DetalleAbastecimientoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-abastecimiento")
public class DetalleAbastecimientoController {

    private final DetalleAbastecimientoService detalleService;

    public DetalleAbastecimientoController(
            DetalleAbastecimientoService detalleService) {
        this.detalleService = detalleService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleAbastecimiento>> listarTodos() {
        return ResponseEntity.ok(detalleService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleAbastecimiento> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(detalleService.buscarPorId(id));
    }

    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<List<DetalleAbastecimiento>> listarPorSolicitud(
            @PathVariable Long solicitudId) {

        return ResponseEntity.ok(
                detalleService.buscarPorSolicitud(solicitudId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DetalleAbastecimiento>> listarPorProducto(
            @PathVariable Long productoId) {

        return ResponseEntity.ok(
                detalleService.buscarPorProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<DetalleAbastecimiento> crear(
            @RequestBody DetalleAbastecimiento detalle) {

        DetalleAbastecimiento nuevoDetalle =
                detalleService.guardar(detalle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoDetalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleAbastecimiento> actualizar(
            @PathVariable Long id,
            @RequestBody DetalleAbastecimiento detalle) {

        detalleService.buscarPorId(id);

        detalle.setId(id);

        DetalleAbastecimiento actualizado =
                detalleService.guardar(detalle);

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        detalleService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}