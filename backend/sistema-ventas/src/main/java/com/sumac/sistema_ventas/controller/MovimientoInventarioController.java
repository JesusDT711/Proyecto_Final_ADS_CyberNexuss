
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.MovimientoInventario;
import com.sumac.sistema_ventas.service.MovimientoInventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(
            MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoInventario>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventario> buscarPorId(
            @PathVariable Long id) {
        return movimientoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoInventario>> listarPorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(
                movimientoService.listarPorProducto(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MovimientoInventario>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                movimientoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoInventario>> listarPorTipo(
            @PathVariable String tipoMovimiento) {
        return ResponseEntity.ok(
                movimientoService.listarPorTipo(tipoMovimiento));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<MovimientoInventario>> listarPorPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {

        if (inicio.isAfter(fin)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                movimientoService.listarPorPeriodo(inicio, fin));
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> crear(
            @RequestBody MovimientoInventario movimiento) {
        MovimientoInventario nuevoMovimiento =
                movimientoService.guardar(movimiento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoMovimiento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (movimientoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
