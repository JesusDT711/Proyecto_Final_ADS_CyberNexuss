package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.Entrega;
import com.sumac.sistema_ventas.model.Pedido;

import com.sumac.sistema_ventas.repository.EntregaRepository;
import com.sumac.sistema_ventas.repository.PedidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;

    public EntregaService(
            EntregaRepository entregaRepository,
            PedidoRepository pedidoRepository) {

        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Entrega> listarTodas() {
        return entregaRepository.findAll();
    }

    public Optional<Entrega> buscarPorId(Long id) {
        return entregaRepository.findById(id);
    }

    public Optional<Entrega> buscarPorPedido(Long pedidoId) {
        return entregaRepository.findByPedidoId(pedidoId);
    }

    public List<Entrega> listarPorEstado(String estado) {
        return entregaRepository.findByEstadoIgnoreCase(estado);
    }

    @Transactional
    public Entrega guardar(Entrega entrega) {

        if (entrega.getPedido() == null
                || entrega.getPedido().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar un pedido");
        }

        if (entrega.getDireccionEntrega() == null
                || entrega.getDireccionEntrega().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dirección de entrega es obligatoria");
        }

        Long pedidoId = entrega.getPedido().getId();

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El pedido no existe"));

        if (!"CONFIRMADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden generar entregas para pedidos confirmados");
        }

        Optional<Entrega> entregaExistente =
                entregaRepository.findByPedidoId(pedidoId);

        if (entrega.getId() == null && entregaExistente.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Este pedido ya tiene una entrega registrada");
        }

        if (entrega.getId() != null) {

            Entrega existente = entregaRepository.findById(entrega.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La entrega no existe"));

            if ("ENTREGADA".equalsIgnoreCase(existente.getEstado())
                    || "CANCELADA".equalsIgnoreCase(existente.getEstado())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede modificar una entrega entregada o cancelada");
            }

            if (entregaExistente.isPresent()
                    && !entregaExistente.get().getId()
                            .equals(entrega.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El pedido ya tiene otra entrega registrada");
            }

            if (entrega.getEstado() == null
                    || entrega.getEstado().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debe indicar un estado para la entrega");
            }

            String estado = entrega.getEstado().toUpperCase();

            if (!List.of("PENDIENTE", "EN_CAMINO",
                    "ENTREGADA", "CANCELADA").contains(estado)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estado de entrega no válido");
            }

            entrega.setEstado(estado);

            if ("ENTREGADA".equals(estado)) {
                entrega.setFechaEntrega(LocalDateTime.now());
            } else {
                entrega.setFechaEntrega(null);
            }

        } else {

            entrega.setPedido(pedido);
            entrega.setEstado("PENDIENTE");
            entrega.setFechaEntrega(null);
        }

        entrega.setPedido(pedido);

        return entregaRepository.save(entrega);
    }

    @Transactional
    public void eliminar(Long id) {

        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La entrega no existe"));

        if ("ENTREGADA".equalsIgnoreCase(entrega.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar una entrega completada");
        }

        entregaRepository.delete(entrega);
    }
}