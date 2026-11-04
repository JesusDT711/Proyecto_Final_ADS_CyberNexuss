package com.sumac.sistema_ventas.controller;

import com.sumac.sistema_ventas.model.Reporte;
import com.sumac.sistema_ventas.service.ReportePdfService;
import com.sumac.sistema_ventas.service.ReporteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

        private final ReporteService reporteService;
        private final ReportePdfService reportePdfService;

        public ReporteController(ReporteService reporteService, ReportePdfService reportePdfService) {
                this.reporteService = reporteService;
                this.reportePdfService = reportePdfService;
        }

        @GetMapping
        public ResponseEntity<List<Reporte>> listarTodos() {
                return ResponseEntity.ok(reporteService.listarTodos());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Reporte> buscarPorId(@PathVariable Long id) {
                return reporteService.buscarPorId(id)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<Reporte>> listarPorUsuario(
                        @PathVariable Long usuarioId) {
                return ResponseEntity.ok(
                                reporteService.listarPorUsuario(usuarioId));
        }

        @GetMapping("/tipo/{tipo}")
        public ResponseEntity<List<Reporte>> listarPorTipo(
                        @PathVariable String tipo) {
                return ResponseEntity.ok(
                                reporteService.listarPorTipo(tipo));
        }

        @GetMapping("/formato/{formato}")
        public ResponseEntity<List<Reporte>> listarPorFormato(
                        @PathVariable String formato) {
                return ResponseEntity.ok(
                                reporteService.listarPorFormato(formato));
        }

        @GetMapping(value = "/inventario/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfInventario() {

                byte[] pdf = reportePdfService.generarReporteInventario();

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-inventario.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @GetMapping(value = "/stock-bajo/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfStockBajo() {

                byte[] pdf = reportePdfService.generarReporteStockBajo();

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-stock-bajo.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @GetMapping(value = "/ventas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfVentas() {

                byte[] pdf = reportePdfService.generarReporteVentas();

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-ventas.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @GetMapping(value = "/movimientos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfMovimientos() {

                byte[] pdf = reportePdfService.generarReporteMovimientos();

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-movimientos.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @GetMapping(value = "/abastecimiento/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfAbastecimiento() {

                byte[] pdf = reportePdfService.generarReporteAbastecimiento();

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-abastecimiento.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @GetMapping(value = "/entregas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generarPdfEntregas() {

                byte[] pdf = reportePdfService.generarReporteEntregas();

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=reporte-entregas.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .contentLength(pdf.length)
                                .body(pdf);
        }

        @PostMapping
        public ResponseEntity<Reporte> crear(
                        @RequestBody Reporte reporte) {
                Reporte nuevo = reporteService.guardar(reporte);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(nuevo);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(@PathVariable Long id) {
                reporteService.eliminar(id);
                return ResponseEntity.noContent().build();
        }
}