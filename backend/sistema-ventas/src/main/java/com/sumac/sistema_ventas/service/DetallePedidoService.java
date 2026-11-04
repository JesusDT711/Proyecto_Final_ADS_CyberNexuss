
package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.DetallePedido;
import com.sumac.sistema_ventas.model.Pedido;
import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.repository.DetallePedidoRepository;
import com.sumac.sistema_ventas.repository.PedidoRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository, PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    public List<DetallePedido> listarTodos() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public List<DetallePedido> listarPorPedido(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId);
    }

    public List<DetallePedido> listarPorProducto(Long productoId) {
        return detallePedidoRepository.findByProductoId(productoId);
    }

    @Transactional
    public DetallePedido guardar(DetallePedido detalle) {

        if (detalle.getPedido() == null
                || detalle.getPedido().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar un pedido válido.");
        }

        if (detalle.getProducto() == null
                || detalle.getProducto().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar un producto válido.");
        }

        if (detalle.getCantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero.");
        }

        Pedido pedido = pedidoRepository
                .findById(detalle.getPedido().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El pedido indicado no existe."));

        Producto producto = productoRepository
                .findById(detalle.getProducto().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto indicado no existe."));

        if (Boolean.FALSE.equals(producto.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede vender un producto inactivo.");
        }

        BigDecimal precioUnitario = producto.getPrecio();

        if (precioUnitario == null
                || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto tiene un precio inválido.");
        }

        BigDecimal subtotal = precioUnitario.multiply(
                BigDecimal.valueOf(detalle.getCantidad()));

        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);

        DetallePedido guardado = detallePedidoRepository.save(detalle);

        actualizarTotalPedido(pedido.getId());

        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        DetallePedido detalle = detallePedidoRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El detalle del pedido no existe."));

        Long pedidoId = detalle.getPedido().getId();

        detallePedidoRepository.delete(detalle);
        detallePedidoRepository.flush();

        actualizarTotalPedido(pedidoId);
    }

    private void actualizarTotalPedido(Long pedidoId) {
        List<DetallePedido> detalles =
                detallePedidoRepository.findByPedidoId(pedidoId);

        BigDecimal total = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El pedido no existe."));

        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }
}