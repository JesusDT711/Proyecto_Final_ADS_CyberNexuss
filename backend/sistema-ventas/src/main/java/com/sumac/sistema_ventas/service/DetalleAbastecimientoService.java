package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.DetalleAbastecimiento;
import com.sumac.sistema_ventas.model.SolicitudAbastecimiento;
import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.repository.DetalleAbastecimientoRepository;
import com.sumac.sistema_ventas.repository.SolicitudAbastecimientoRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DetalleAbastecimientoService {

    private final DetalleAbastecimientoRepository detalleRepository;
    private final SolicitudAbastecimientoRepository solicitudRepository;
    private final ProductoRepository productoRepository;

    public DetalleAbastecimientoService(
            DetalleAbastecimientoRepository detalleRepository,
            SolicitudAbastecimientoRepository solicitudRepository,
            ProductoRepository productoRepository) {

        this.detalleRepository = detalleRepository;
        this.solicitudRepository = solicitudRepository;
        this.productoRepository = productoRepository;
    }

    public List<DetalleAbastecimiento> listarTodos() {
        return detalleRepository.findAll();
    }

    public DetalleAbastecimiento buscarPorId(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Detalle de abastecimiento no encontrado"));
    }

    public List<DetalleAbastecimiento> buscarPorSolicitud(Long solicitudId) {
        return detalleRepository.findBySolicitud_Id(solicitudId);
    }

    public List<DetalleAbastecimiento> buscarPorProducto(Long productoId) {
        return detalleRepository.findByProductoId(productoId);
    }

    @Transactional
    public DetalleAbastecimiento guardar(DetalleAbastecimiento detalle) {

        if (detalle.getSolicitud() == null
                || detalle.getSolicitud().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar una solicitud de abastecimiento");
        }

        if (detalle.getProducto() == null
                || detalle.getProducto().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar un producto");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor que cero");
        }

        if (detalle.getCostoUnitario() == null
                || detalle.getCostoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El costo unitario debe ser mayor que cero");
        }

        Long solicitudId = detalle.getSolicitud().getId();
        Long productoId = detalle.getProducto().getId();

        SolicitudAbastecimiento solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitud de abastecimiento no encontrada"));

        if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden agregar detalles a solicitudes pendientes");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede abastecer un producto inactivo");
        }

        detalle.setSolicitud(solicitud);
        detalle.setProducto(producto);

        return detalleRepository.save(detalle);
    }

    @Transactional
    public void eliminar(Long id) {

        DetalleAbastecimiento detalle = buscarPorId(id);

        if (!"PENDIENTE".equalsIgnoreCase(
                detalle.getSolicitud().getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden eliminar detalles de una solicitud recibida");
        }

        detalleRepository.delete(detalle);
    }
}