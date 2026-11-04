package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.AlertaStock;
import com.sumac.sistema_ventas.service.AlertaStockService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas-stock")
public class AlertaStockController {

    private final AlertaStockService alertaService;

    public AlertaStockController(AlertaStockService alertaService) {
        this.alertaService = alertaService;
    }

    // Listar todas las alertas
    @GetMapping
    public ResponseEntity<List<AlertaStock>> listarTodas() {
        return ResponseEntity.ok(alertaService.listarTodas());
    }

    // Listar alertas pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<AlertaStock>> listarPendientes() {
        return ResponseEntity.ok(alertaService.listarPendientes());
    }

    // Buscar alerta por ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertaStock> buscarPorId(
            @PathVariable Long id) {

        return alertaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar alertas por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AlertaStock>> listarPorProducto(
            @PathVariable Long productoId) {

        return ResponseEntity.ok(
                alertaService.listarPorProducto(productoId));
    }

    // Atender una alerta manualmente
    @PatchMapping("/{id}/atender")
    public ResponseEntity<AlertaStock> atender(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                alertaService.atenderAlerta(id));
    }

    // Eliminar alerta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (alertaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        alertaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}