
package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.SolicitudAbastecimiento;
import com.sumac.sistema_ventas.model.DetalleAbastecimiento;
import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.model.MovimientoInventario;

import com.sumac.sistema_ventas.repository.SolicitudAbastecimientoRepository;
import com.sumac.sistema_ventas.repository.DetalleAbastecimientoRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudAbastecimientoService {

    private final SolicitudAbastecimientoRepository solicitudRepository;
    private final DetalleAbastecimientoRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoService;

    public SolicitudAbastecimientoService(
            SolicitudAbastecimientoRepository solicitudRepository,
            DetalleAbastecimientoRepository detalleRepository,
            ProductoRepository productoRepository,
            MovimientoInventarioService movimientoService) {

        this.solicitudRepository = solicitudRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.movimientoService = movimientoService;
    }

    public List<SolicitudAbastecimiento> listarTodos() {
        return solicitudRepository.findAll();
    }

    public Optional<SolicitudAbastecimiento> buscarPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    public List<SolicitudAbastecimiento> listarPorProveedor(
            Long proveedorId) {
        return solicitudRepository.findByProveedorId(proveedorId);
    }

    public List<SolicitudAbastecimiento> listarPorUsuario(
            Long usuarioId) {
        return solicitudRepository.findByUsuarioId(usuarioId);
    }

    public List<SolicitudAbastecimiento> listarPorEstado(String estado) {
        return solicitudRepository.findByEstadoIgnoreCase(estado);
    }

    public SolicitudAbastecimiento guardar(
            SolicitudAbastecimiento solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public SolicitudAbastecimiento recibirSolicitud(Long solicitudId) {

        // 1. Buscar la solicitud
        SolicitudAbastecimiento solicitud =
                solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La solicitud de abastecimiento no existe"));

        // 2. Verificar que esté pendiente
        if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden recibir solicitudes pendientes");
        }

        // 3. Obtener los detalles
        List<DetalleAbastecimiento> detalles =
                detalleRepository.findBySolicitud_Id(solicitudId);

        if (detalles.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede recibir una solicitud sin detalles");
        }

        // 4. Validar todos los detalles antes de modificar el stock
        for (DetalleAbastecimiento detalle : detalles) {

            if (detalle.getCantidad() == null
                    || detalle.getCantidad() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La cantidad debe ser mayor que cero");
            }

            if (detalle.getProducto() == null
                    || detalle.getProducto().getId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debe indicar un producto válido");
            }

            Producto producto = productoRepository.findById(
                    detalle.getProducto().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno de los productos de la solicitud no existe"));

            if (!producto.getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El producto " + producto.getNombre()
                                + " está inactivo");
            }
        }

        // 5. Registrar una ENTRADA por cada detalle
        for (DetalleAbastecimiento detalle : detalles) {

            MovimientoInventario movimiento =
                    new MovimientoInventario();

            movimiento.setProducto(detalle.getProducto());
            movimiento.setUsuario(solicitud.getUsuario());
            movimiento.setTipoMovimiento("ENTRADA");
            movimiento.setCantidad(detalle.getCantidad());
            movimiento.setFechaMovimiento(LocalDateTime.now());

            movimiento.setObservacion(
                    "Recepción de solicitud de abastecimiento N.º "
                            + solicitudId);

            movimientoService.guardar(movimiento);
        }

        // 6. Actualizar el estado de la solicitud
        solicitud.setEstado("RECIBIDA");

        return solicitudRepository.save(solicitud);
    }

    public void eliminar(Long id) {
        solicitudRepository.deleteById(id);
    }
}
