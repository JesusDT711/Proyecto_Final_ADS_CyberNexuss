package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.Pedido;
import com.sumac.sistema_ventas.model.DetallePedido;
import com.sumac.sistema_ventas.model.Producto;

import com.sumac.sistema_ventas.repository.PedidoRepository;
import com.sumac.sistema_ventas.repository.DetallePedidoRepository;
import com.sumac.sistema_ventas.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final AlertaStockService alertaStockService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository,
            AlertaStockService alertaStockService) {

        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.alertaStockService = alertaStockService;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepository.findByEstadoIgnoreCase(estado);
    }

    public List<Pedido> listarPorPeriodo(
            LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByFechaPedidoBetween(inicio, fin);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido confirmarPedido(Long pedidoId) {

        // 1. Buscar el pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El pedido no existe"));

        // 2. Verificar que esté pendiente
        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden confirmar pedidos pendientes");
        }

        // 3. Obtener los detalles
        List<DetallePedido> detalles =
                detallePedidoRepository.findByPedidoId(pedidoId);

        if (detalles.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede confirmar un pedido sin detalles");
        }

        // 4. Validar todos los productos antes de descontar
        for (DetallePedido detalle : detalles) {

            Producto producto = productoRepository.findById(
                    detalle.getProducto().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró uno de los productos del pedido"));

            if (!producto.getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El producto " + producto.getNombre()
                                + " está inactivo");
            }

            if (detalle.getCantidad() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La cantidad del producto debe ser mayor que cero");
            }

            if (producto.getStock() < detalle.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para el producto: "
                                + producto.getNombre());
            }
        }

        // 5. Descontar el inventario y verificar las alertas
        for (DetallePedido detalle : detalles) {

            Producto producto = productoRepository.findById(
                    detalle.getProducto().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró uno de los productos"));

            producto.setStock(
                    producto.getStock() - detalle.getCantidad()
            );

            productoRepository.save(producto);

            // Comprobar si el nuevo stock requiere una alerta
            alertaStockService.verificarStock(producto.getId());
        }

        // 6. Cambiar el estado del pedido
        pedido.setEstado("CONFIRMADO");

        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}