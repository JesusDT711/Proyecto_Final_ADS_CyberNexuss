package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.Reporte;
import com.sumac.sistema_ventas.model.Usuario;
import com.sumac.sistema_ventas.repository.ReporteRepository;
import com.sumac.sistema_ventas.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(
            ReporteRepository reporteRepository,
            UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Reporte> listarTodos() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> buscarPorId(Long id) {
        return reporteRepository.findById(id);
    }

    public List<Reporte> listarPorUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioId(usuarioId);
    }

    public List<Reporte> listarPorTipo(String tipo) {
        return reporteRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Reporte> listarPorFormato(String formato) {
        return reporteRepository.findByFormatoIgnoreCase(formato);
    }

    @Transactional
    public Reporte guardar(Reporte reporte) {

        if (reporte.getUsuario() == null ||
                reporte.getUsuario().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el usuario que genera el reporte."
            );
        }

        if (reporte.getTipo() == null ||
                reporte.getTipo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de reporte es obligatorio."
            );
        }

        if (reporte.getTitulo() == null ||
                reporte.getTitulo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El título del reporte es obligatorio."
            );
        }

        String formato = reporte.getFormato() == null
                ? "PDF"
                : reporte.getFormato().trim().toUpperCase();

        if (!formato.equals("PDF") &&
                !formato.equals("EXCEL") &&
                !formato.equals("CSV")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El formato debe ser PDF, EXCEL o CSV."
            );
        }

        Long usuarioId = reporte.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El usuario indicado no existe."
                ));

        reporte.setUsuario(usuario);
        reporte.setTipo(reporte.getTipo().trim());
        reporte.setTitulo(reporte.getTitulo().trim());
        reporte.setFormato(formato);
        reporte.setFechaGeneracion(LocalDateTime.now());

        return reporteRepository.save(reporte);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!reporteRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El reporte no existe."
            );
        }

        reporteRepository.deleteById(id);
    }
}