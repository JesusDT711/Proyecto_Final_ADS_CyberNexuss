
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.DetallePedido;
import com.sumac.sistema_ventas.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    public DetallePedidoController(
            DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public ResponseEntity<List<DetallePedido>> listarTodos() {
        return ResponseEntity.ok(
                detallePedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> buscarPorId(
            @PathVariable Long id) {
        return detallePedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedido>> listarPorPedido(
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(
                detallePedidoService.listarPorPedido(pedidoId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DetallePedido>> listarPorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(
                detallePedidoService.listarPorProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<DetallePedido> crear(
            @RequestBody DetallePedido detalle) {
        DetallePedido nuevoDetalle =
                detallePedidoService.guardar(detalle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoDetalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> actualizar(
            @PathVariable Long id,
            @RequestBody DetallePedido detalle) {

        return detallePedidoService.buscarPorId(id)
                .map(existente -> {
                    detalle.setId(id);
                    DetallePedido actualizado =
                            detallePedidoService.guardar(detalle);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {
        if (detallePedidoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        detallePedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
