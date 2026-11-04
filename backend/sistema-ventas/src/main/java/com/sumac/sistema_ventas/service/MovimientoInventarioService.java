package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.MovimientoInventario;
import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.repository.MovimientoInventarioRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final AlertaStockService alertaStockService;

    public MovimientoInventarioService(
            MovimientoInventarioRepository movimientoRepository,
            ProductoRepository productoRepository,
            AlertaStockService alertaStockService) {

        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.alertaStockService = alertaStockService;
    }

    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }

    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    public List<MovimientoInventario> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId);
    }

    public List<MovimientoInventario> listarPorUsuario(Long usuarioId) {
        return movimientoRepository.findByUsuarioId(usuarioId);
    }

    public List<MovimientoInventario> listarPorTipo(String tipoMovimiento) {
        return movimientoRepository.findByTipoMovimientoIgnoreCase(tipoMovimiento);
    }

    public List<MovimientoInventario> listarPorPeriodo(
            LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaMovimientoBetween(inicio, fin);
    }

    @Transactional
    public MovimientoInventario guardar(MovimientoInventario movimiento) {

        if (movimiento.getProducto() == null
                || movimiento.getProducto().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar un producto válido.");
        }

        if (movimiento.getCantidad() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor que cero.");
        }

        if (movimiento.getTipoMovimiento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el tipo de movimiento.");
        }

        String tipo = movimiento.getTipoMovimiento().trim().toUpperCase();

        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo debe ser ENTRADA o SALIDA.");
        }

        Producto producto = productoRepository.findById(
                movimiento.getProducto().getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "El producto indicado no existe."));

        int stockActual = producto.getStock();
        int cantidad = movimiento.getCantidad();

        if (tipo.equals("SALIDA") && cantidad > stockActual) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock insuficiente. Stock disponible: " + stockActual);
        }

        if (tipo.equals("ENTRADA")) {
            producto.setStock(stockActual + cantidad);
        } else {
            producto.setStock(stockActual - cantidad);
        }

        productoRepository.save(producto);

        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(tipo);

        if (movimiento.getFechaMovimiento() == null) {
            movimiento.setFechaMovimiento(LocalDateTime.now());
        }

        MovimientoInventario movimientoGuardado =
                movimientoRepository.save(movimiento);

        // Verificar el stock actualizado y gestionar la alerta.
        alertaStockService.verificarStock(producto.getId());

        return movimientoGuardado;
    }

    public void eliminar(Long id) {
        movimientoRepository.deleteById(id);
    }
}