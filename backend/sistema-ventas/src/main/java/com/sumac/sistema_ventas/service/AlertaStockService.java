package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.AlertaStock;
import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.repository.AlertaStockRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AlertaStockService {

    private final AlertaStockRepository alertaRepository;
    private final ProductoRepository productoRepository;

    public AlertaStockService(
            AlertaStockRepository alertaRepository,
            ProductoRepository productoRepository) {
        this.alertaRepository = alertaRepository;
        this.productoRepository = productoRepository;
    }

    public List<AlertaStock> listarTodas() {
        return alertaRepository.findAll();
    }

    public Optional<AlertaStock> buscarPorId(Long id) {
        return alertaRepository.findById(id);
    }

    public List<AlertaStock> listarPendientes() {
        return alertaRepository.findByAtendidaFalse();
    }

    public List<AlertaStock> listarPorProducto(Long productoId) {
        return alertaRepository.findByProductoId(productoId);
    }

    @Transactional
    public void verificarStock(Long productoId) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con ID: " + productoId));

        Integer stock = producto.getStock();
        Integer stockMinimo = producto.getStockMinimo();

        if (stock == null || stockMinimo == null) {
            throw new RuntimeException(
                    "El producto no tiene configurado su stock correctamente.");
        }

        Optional<AlertaStock> alertaPendiente = alertaRepository.findFirstByProductoIdAndAtendidaFalse(productoId);

        if (stock <= stockMinimo) {

            if (alertaPendiente.isEmpty()) {

                AlertaStock alerta = new AlertaStock();

                alerta.setProducto(producto);
                alerta.setAtendida(false);
                alerta.setMensaje(
                        "Stock bajo para el producto " + producto.getNombre()
                                + ". Disponible: " + stock
                                + " unidades. Stock mínimo: " + stockMinimo + " unidades.");

                alertaRepository.save(alerta);
            }

        } else {

            if (alertaPendiente.isPresent()) {

                AlertaStock alerta = alertaPendiente.get();
                alerta.setAtendida(true);

                alertaRepository.save(alerta);
            }
        }
    }

    public AlertaStock guardar(AlertaStock alerta) {
        return alertaRepository.save(alerta);
    }

    @Transactional
    public AlertaStock atenderAlerta(Long id) {

        AlertaStock alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La alerta no existe."));

        if (Boolean.TRUE.equals(alerta.getAtendida())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta alerta ya fue atendida.");
        }

        Producto producto = productoRepository.findById(
                alerta.getProducto().getId()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El producto asociado no existe."));

        if (producto.getStock() <= producto.getStockMinimo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede atender la alerta porque el stock todavía está "
                            + "en el mínimo o por debajo de él.");
        }

        alerta.setAtendida(true);

        return alertaRepository.save(alerta);
    }

    public void eliminar(Long id) {
        alertaRepository.deleteById(id);
    }
}