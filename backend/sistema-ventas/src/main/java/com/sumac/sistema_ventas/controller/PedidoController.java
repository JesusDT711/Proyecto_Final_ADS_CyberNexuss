
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.Pedido;
import com.sumac.sistema_ventas.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(
            @PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(
                pedidoService.listarPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> listarPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                pedidoService.listarPorEstado(estado));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Pedido>> listarPorPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {

        if (inicio.isAfter(fin)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                pedidoService.listarPorPeriodo(inicio, fin));
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(
            @RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.guardar(pedido);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoPedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return pedidoService.buscarPorId(id)
                .map(pedidoExistente -> {
                    pedido.setId(id);
                    Pedido actualizado = pedidoService.guardar(pedido);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (pedidoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Pedido> confirmar(
            @PathVariable Long id) {

        Pedido pedidoConfirmado = pedidoService.confirmarPedido(id);

        return ResponseEntity.ok(pedidoConfirmado);
    }

}
