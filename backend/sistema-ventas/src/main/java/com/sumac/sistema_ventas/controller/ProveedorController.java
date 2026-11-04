
package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.Proveedor;
import com.sumac.sistema_ventas.service.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Proveedor>> listarActivos() {
        return ResponseEntity.ok(proveedorService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> buscarPorId(@PathVariable Long id) {
        return proveedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Proveedor>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(
                proveedorService.buscarPorNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(
            @RequestBody Proveedor proveedor) {
        Proveedor nuevoProveedor = proveedorService.guardar(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(
            @PathVariable Long id,
            @RequestBody Proveedor proveedor) {

        return proveedorService.buscarPorId(id)
                .map(proveedorExistente -> {
                    proveedor.setId(id);
                    Proveedor actualizado =
                            proveedorService.guardar(proveedor);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
