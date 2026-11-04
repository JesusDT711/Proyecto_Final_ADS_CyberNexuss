package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.Entrega;
import com.sumac.sistema_ventas.service.EntregaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @GetMapping
    public ResponseEntity<List<Entrega>> listarTodas() {
        return ResponseEntity.ok(entregaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrega> buscarPorId(
            @PathVariable Long id) {

        return entregaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Entrega> buscarPorPedido(
            @PathVariable Long pedidoId) {

        return entregaService.buscarPorPedido(pedidoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Entrega>> listarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                entregaService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<Entrega> crear(
            @RequestBody Entrega entrega) {

        Entrega nueva = entregaService.guardar(entrega);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entrega> actualizar(
            @PathVariable Long id,
            @RequestBody Entrega entrega) {

        entregaService.buscarPorId(id)
                .orElseThrow(() -> new org.springframework.web.server
                        .ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La entrega no existe"));

        entrega.setId(id);

        Entrega actualizada = entregaService.guardar(entrega);

        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        entregaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}