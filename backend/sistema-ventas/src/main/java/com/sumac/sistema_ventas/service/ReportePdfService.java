
package com.sumac.sistema_ventas.service;

import com.sumac.sistema_ventas.model.Producto;
import com.sumac.sistema_ventas.repository.ProductoRepository;
import com.sumac.sistema_ventas.repository.PedidoRepository;
import com.sumac.sistema_ventas.repository.DetallePedidoRepository;
import com.sumac.sistema_ventas.model.Pedido;
import com.sumac.sistema_ventas.model.DetallePedido;
import com.sumac.sistema_ventas.model.MovimientoInventario;
import com.sumac.sistema_ventas.repository.MovimientoInventarioRepository;
import com.sumac.sistema_ventas.model.SolicitudAbastecimiento;
import com.sumac.sistema_ventas.model.DetalleAbastecimiento;
import com.sumac.sistema_ventas.repository.SolicitudAbastecimientoRepository;
import com.sumac.sistema_ventas.repository.DetalleAbastecimientoRepository;
import com.sumac.sistema_ventas.model.Entrega;
import com.sumac.sistema_ventas.repository.EntregaRepository;
import java.math.BigDecimal;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportePdfService {

        private final ProductoRepository productoRepository;
        private final PedidoRepository pedidoRepository;
        private final DetallePedidoRepository detallePedidoRepository;
        private final MovimientoInventarioRepository movimientoInventarioRepository;
        private final SolicitudAbastecimientoRepository solicitudAbastecimientoRepository;
        private final DetalleAbastecimientoRepository detalleAbastecimientoRepository;
        private final EntregaRepository entregaRepository;

        public ReportePdfService(ProductoRepository productoRepository, PedidoRepository pedidoRepository,
                        DetallePedidoRepository detallePedidoRepository,
                        MovimientoInventarioRepository movimientoInventarioRepository,
                        SolicitudAbastecimientoRepository solicitudAbastecimientoRepository,
                        DetalleAbastecimientoRepository detalleAbastecimientoRepository,
                        EntregaRepository entregaRepository) {
                this.productoRepository = productoRepository;
                this.pedidoRepository = pedidoRepository;
                this.detallePedidoRepository = detallePedidoRepository;
                this.movimientoInventarioRepository = movimientoInventarioRepository;
                this.solicitudAbastecimientoRepository = solicitudAbastecimientoRepository;
                this.detalleAbastecimientoRepository = detalleAbastecimientoRepository;
                this.entregaRepository = entregaRepository;
        }

        public byte[] generarReporteInventario() {
                List<Producto> productos = productoRepository.findAll();

                try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Document documento = new Document(PageSize.A4.rotate());
                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font encabezadoFont = new Font(Font.HELVETICA, 9, Font.BOLD);
                        Font contenidoFont = new Font(Font.HELVETICA, 8);

                        Paragraph titulo = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.",
                                        tituloFont);
                        titulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(titulo);

                        Paragraph subtitulo = new Paragraph(
                                        "Reporte general de inventario",
                                        new Font(Font.HELVETICA, 14, Font.BOLD));
                        subtitulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(subtitulo);

                        String fecha = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                        documento.add(new Paragraph("Fecha de generación: " + fecha));
                        documento.add(new Paragraph("Total de productos: " + productos.size()));
                        documento.add(new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(
                                        new float[] { 1, 2, 4, 3, 2, 2, 2 });
                        tabla.setWidthPercentage(100);

                        agregarCelda(tabla, "ID", encabezadoFont);
                        agregarCelda(tabla, "Código", encabezadoFont);
                        agregarCelda(tabla, "Producto", encabezadoFont);
                        agregarCelda(tabla, "Categoría", encabezadoFont);
                        agregarCelda(tabla, "Precio", encabezadoFont);
                        agregarCelda(tabla, "Stock", encabezadoFont);
                        agregarCelda(tabla, "Stock mínimo", encabezadoFont);

                        for (Producto producto : productos) {
                                agregarCelda(tabla, texto(producto.getId()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getCodigo()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getNombre()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getCategoria()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getPrecio()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getStock()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getStockMinimo()), contenidoFont);
                        }

                        documento.add(tabla);
                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException | java.io.IOException e) {
                        throw new IllegalStateException(
                                        "No se pudo generar el reporte PDF de inventario.", e);
                }
        }

        public byte[] generarReporteStockBajo() {

                List<Producto> productos = productoRepository.findProductosActivosConStockBajo();

                try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Document documento = new Document(PageSize.A4.rotate());
                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font subtituloFont = new Font(Font.HELVETICA, 14, Font.BOLD);
                        Font encabezadoFont = new Font(Font.HELVETICA, 9, Font.BOLD);
                        Font contenidoFont = new Font(Font.HELVETICA, 8);

                        Paragraph titulo = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.",
                                        tituloFont);
                        titulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(titulo);

                        Paragraph subtitulo = new Paragraph(
                                        "Reporte de productos con stock bajo",
                                        subtituloFont);
                        subtitulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(subtitulo);

                        String fecha = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                        documento.add(new Paragraph("Fecha de generación: " + fecha));
                        documento.add(new Paragraph(
                                        "Productos que requieren reposición: " + productos.size()));
                        documento.add(new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(
                                        new float[] { 1, 2, 4, 3, 2, 2, 2 });
                        tabla.setWidthPercentage(100);

                        agregarCelda(tabla, "ID", encabezadoFont);
                        agregarCelda(tabla, "Código", encabezadoFont);
                        agregarCelda(tabla, "Producto", encabezadoFont);
                        agregarCelda(tabla, "Categoría", encabezadoFont);
                        agregarCelda(tabla, "Precio", encabezadoFont);
                        agregarCelda(tabla, "Stock actual", encabezadoFont);
                        agregarCelda(tabla, "Stock mínimo", encabezadoFont);

                        for (Producto producto : productos) {
                                agregarCelda(tabla, texto(producto.getId()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getCodigo()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getNombre()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getCategoria()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getPrecio()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getStock()), contenidoFont);
                                agregarCelda(tabla, texto(producto.getStockMinimo()), contenidoFont);
                        }

                        documento.add(tabla);
                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException | java.io.IOException e) {
                        throw new IllegalStateException(
                                        "No se pudo generar el reporte PDF de stock bajo.", e);
                }
        }

        public byte[] generarReporteVentas(java.time.LocalDate inicio, java.time.LocalDate fin) {

                List<Pedido> pedidos = pedidoRepository.findAll().stream().filter(p -> enRango(p.getFechaPedido(), inicio, fin)).collect(java.util.stream.Collectors.toList());

                try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Document documento = new Document(PageSize.A4.rotate());
                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font subtituloFont = new Font(Font.HELVETICA, 14, Font.BOLD);
                        Font encabezadoFont = new Font(Font.HELVETICA, 9, Font.BOLD);
                        Font contenidoFont = new Font(Font.HELVETICA, 8);

                        Paragraph titulo = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.",
                                        tituloFont);
                        titulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(titulo);

                        Paragraph subtitulo = new Paragraph(
                                        "Reporte general de ventas",
                                        subtituloFont);
                        subtitulo.setAlignment(Element.ALIGN_CENTER);
                        documento.add(subtitulo);

                        String fecha = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                        documento.add(new Paragraph("Fecha de generación: " + fecha));
                        documento.add(new Paragraph("Total de pedidos: " + pedidos.size()));
                        documento.add(new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(
                                        new float[] { 1, 3, 2, 2, 2, 2 });
                        tabla.setWidthPercentage(100);

                        agregarCelda(tabla, "N.º Pedido", encabezadoFont);
                        agregarCelda(tabla, "Cliente", encabezadoFont);
                        agregarCelda(tabla, "Fecha", encabezadoFont);
                        agregarCelda(tabla, "Estado", encabezadoFont);
                        agregarCelda(tabla, "Unidades", encabezadoFont);
                        agregarCelda(tabla, "Total", encabezadoFont);

                        for (Pedido pedido : pedidos) {

                                List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());

                                int unidades = detalles.stream()
                                                .mapToInt(detalle -> detalle.getCantidad() == null
                                                                ? 0
                                                                : detalle.getCantidad())
                                                .sum();

                                String nombreCliente = pedido.getCliente() != null
                                                ? texto(pedido.getCliente().getNombre())
                                                : "-";

                                String fechaPedido = pedido.getFechaPedido() != null
                                                ? pedido.getFechaPedido().format(
                                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                                : "-";

                                agregarCelda(tabla, texto(pedido.getId()), contenidoFont);
                                agregarCelda(tabla, nombreCliente, contenidoFont);
                                agregarCelda(tabla, fechaPedido, contenidoFont);
                                agregarCelda(tabla, texto(pedido.getEstado()), contenidoFont);
                                agregarCelda(tabla, String.valueOf(unidades), contenidoFont);
                                agregarCelda(tabla, texto(pedido.getTotal()), contenidoFont);
                        }

                        documento.add(tabla);
                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException | java.io.IOException e) {
                        throw new IllegalStateException(
                                        "No se pudo generar el reporte PDF de ventas.", e);
                }
        }

        public byte[] generarReporteMovimientos(java.time.LocalDate inicio, java.time.LocalDate fin) {

                List<MovimientoInventario> movimientos = movimientoInventarioRepository.findAll().stream().filter(m -> enRango(m.getFechaMovimiento(), inicio, fin)).collect(java.util.stream.Collectors.toList());

                ByteArrayOutputStream salida = new ByteArrayOutputStream();

                Document documento = new Document(PageSize.A4.rotate());

                try {

                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font subtitulo = new Font(Font.HELVETICA, 14, Font.BOLD);
                        Font normal = new Font(Font.HELVETICA, 11);
                        Font encabezado = new Font(Font.HELVETICA, 10, Font.BOLD);

                        Paragraph empresa = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.", titulo);
                        empresa.setAlignment(Element.ALIGN_CENTER);
                        documento.add(empresa);

                        Paragraph nombreReporte = new Paragraph(
                                        "Reporte general de movimientos de inventario",
                                        subtitulo);
                        nombreReporte.setAlignment(Element.ALIGN_CENTER);
                        documento.add(nombreReporte);

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "Fecha de generación: "
                                                        + LocalDateTime.now().format(
                                                                        DateTimeFormatter.ofPattern(
                                                                                        "dd/MM/yyyy HH:mm:ss")),
                                        normal));

                        int totalEntradas = 0;
                        int totalSalidas = 0;
                        int cantidadEntradas = 0;
                        int cantidadSalidas = 0;

                        for (MovimientoInventario movimiento : movimientos) {

                                if ("ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                                        totalEntradas++;
                                        cantidadEntradas += movimiento.getCantidad();

                                } else if ("SALIDA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                                        totalSalidas++;
                                        cantidadSalidas += movimiento.getCantidad();
                                }
                        }

                        documento.add(new Paragraph(
                                        "Total de movimientos: " + movimientos.size(), normal));

                        documento.add(new Paragraph(
                                        "Movimientos de entrada: " + totalEntradas
                                                        + " | Unidades ingresadas: " + cantidadEntradas,
                                        normal));

                        documento.add(new Paragraph(
                                        "Movimientos de salida: " + totalSalidas
                                                        + " | Unidades retiradas: " + cantidadSalidas,
                                        normal));

                        documento.add(new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(7);
                        tabla.setWidthPercentage(100);

                        tabla.setWidths(new float[] {
                                        0.7f, 1.5f, 1.5f, 1.2f, 1.0f, 1.5f, 2.5f
                        });

                        agregarCelda(tabla, "N.º Movimiento", encabezado);
                        agregarCelda(tabla, "Producto", encabezado);
                        agregarCelda(tabla, "Código", encabezado);
                        agregarCelda(tabla, "Tipo", encabezado);
                        agregarCelda(tabla, "Cantidad", encabezado);
                        agregarCelda(tabla, "Fecha", encabezado);
                        agregarCelda(tabla, "Observación", encabezado);

                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        for (MovimientoInventario movimiento : movimientos) {

                                agregarCelda(tabla, texto(movimiento.getId()), normal);

                                agregarCelda(
                                                tabla,
                                                texto(movimiento.getProducto().getNombre()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(movimiento.getProducto().getCodigo()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(movimiento.getTipoMovimiento()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(movimiento.getCantidad()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                movimiento.getFechaMovimiento() != null
                                                                ? movimiento.getFechaMovimiento().format(formatoFecha)
                                                                : "Sin fecha",
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(movimiento.getObservacion()),
                                                normal);
                        }

                        documento.add(tabla);
                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException e) {
                        throw new RuntimeException(
                                        "Error al generar el reporte de movimientos", e);
                }
        }

        public byte[] generarReporteAbastecimiento(java.time.LocalDate inicio, java.time.LocalDate fin) {

                List<SolicitudAbastecimiento> solicitudes = solicitudAbastecimientoRepository.findAll().stream().filter(x -> enRango(x.getFechaSolicitud(), inicio, fin)).collect(java.util.stream.Collectors.toList());

                ByteArrayOutputStream salida = new ByteArrayOutputStream();
                Document documento = new Document(PageSize.A4.rotate());

                try {
                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font subtitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
                        Font normal = new Font(Font.HELVETICA, 10);
                        Font encabezado = new Font(Font.HELVETICA, 9, Font.BOLD);

                        Paragraph empresa = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.", titulo);
                        empresa.setAlignment(Element.ALIGN_CENTER);
                        documento.add(empresa);

                        Paragraph nombreReporte = new Paragraph(
                                        "Reporte general de abastecimiento", subtitulo);
                        nombreReporte.setAlignment(Element.ALIGN_CENTER);
                        documento.add(nombreReporte);

                        documento.add(new Paragraph(" "));

                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        documento.add(new Paragraph(
                                        "Fecha de generación: "
                                                        + LocalDateTime.now().format(formatoFecha),
                                        normal));

                        int pendientes = 0;
                        int recibidas = 0;
                        BigDecimal costoTotal = BigDecimal.ZERO;

                        PdfPTable tabla = new PdfPTable(8);
                        tabla.setWidthPercentage(100);
                        tabla.setWidths(new float[] {
                                        0.7f, 1.3f, 1.5f, 1.2f,
                                        1.8f, 0.8f, 1.2f, 1.3f
                        });

                        agregarCelda(tabla, "N.º", encabezado);
                        agregarCelda(tabla, "Proveedor", encabezado);
                        agregarCelda(tabla, "Fecha", encabezado);
                        agregarCelda(tabla, "Estado", encabezado);
                        agregarCelda(tabla, "Producto", encabezado);
                        agregarCelda(tabla, "Cantidad", encabezado);
                        agregarCelda(tabla, "Costo unitario", encabezado);
                        agregarCelda(tabla, "Subtotal", encabezado);

                        for (SolicitudAbastecimiento solicitud : solicitudes) {

                                if ("PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
                                        pendientes++;
                                }

                                if ("RECIBIDA".equalsIgnoreCase(solicitud.getEstado())) {
                                        recibidas++;
                                }

                                List<DetalleAbastecimiento> detalles = detalleAbastecimientoRepository
                                                .findBySolicitud_Id(solicitud.getId());

                                if (detalles.isEmpty()) {
                                        agregarCelda(tabla, texto(solicitud.getId()), normal);
                                        agregarCelda(
                                                        tabla,
                                                        texto(solicitud.getProveedor().getNombre()),
                                                        normal);
                                        agregarCelda(
                                                        tabla,
                                                        solicitud.getFechaSolicitud() != null
                                                                        ? solicitud.getFechaSolicitud()
                                                                                        .format(formatoFecha)
                                                                        : "Sin fecha",
                                                        normal);
                                        agregarCelda(tabla, texto(solicitud.getEstado()), normal);
                                        agregarCelda(tabla, "Sin productos", normal);
                                        agregarCelda(tabla, "0", normal);
                                        agregarCelda(tabla, "0.00", normal);
                                        agregarCelda(tabla, "0.00", normal);
                                }

                                for (DetalleAbastecimiento detalle : detalles) {

                                        BigDecimal subtotal = detalle.getCostoUnitario()
                                                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

                                        costoTotal = costoTotal.add(subtotal);

                                        agregarCelda(tabla, texto(solicitud.getId()), normal);
                                        agregarCelda(
                                                        tabla,
                                                        texto(solicitud.getProveedor().getNombre()),
                                                        normal);
                                        agregarCelda(
                                                        tabla,
                                                        solicitud.getFechaSolicitud() != null
                                                                        ? solicitud.getFechaSolicitud()
                                                                                        .format(formatoFecha)
                                                                        : "Sin fecha",
                                                        normal);
                                        agregarCelda(tabla, texto(solicitud.getEstado()), normal);
                                        agregarCelda(
                                                        tabla,
                                                        texto(detalle.getProducto().getNombre()),
                                                        normal);
                                        agregarCelda(
                                                        tabla,
                                                        texto(detalle.getCantidad()),
                                                        normal);
                                        agregarCelda(
                                                        tabla,
                                                        detalle.getCostoUnitario().toPlainString(),
                                                        normal);
                                        agregarCelda(tabla, subtotal.toPlainString(), normal);
                                }
                        }

                        documento.add(tabla);
                        documento.add(new Paragraph(" "));

                        documento.add(new Paragraph(
                                        "Total de solicitudes: " + solicitudes.size(), normal));

                        documento.add(new Paragraph(
                                        "Solicitudes pendientes: " + pendientes, normal));

                        documento.add(new Paragraph(
                                        "Solicitudes recibidas: " + recibidas, normal));

                        documento.add(new Paragraph(
                                        "Costo total solicitado: " + costoTotal.toPlainString(),
                                        subtitulo));

                        documento.close();
                        return salida.toByteArray();

                } catch (DocumentException e) {
                        throw new RuntimeException(
                                        "Error al generar el reporte de abastecimiento", e);
                }
        }

        public byte[] generarReporteEntregas(java.time.LocalDate inicio, java.time.LocalDate fin) {

                List<Entrega> entregas = entregaRepository.findAll().stream().filter(x -> enRango(x.getFechaProgramada(), inicio, fin)).collect(java.util.stream.Collectors.toList());

                ByteArrayOutputStream salida = new ByteArrayOutputStream();
                Document documento = new Document(PageSize.A4.rotate());

                try {
                        PdfWriter.getInstance(documento, salida);
                        documento.open();

                        Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
                        Font subtitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
                        Font normal = new Font(Font.HELVETICA, 9);
                        Font encabezado = new Font(Font.HELVETICA, 9, Font.BOLD);

                        Paragraph empresa = new Paragraph(
                                        "INVERSIONES SUMAC YANAHUARA S.R.L.", titulo);
                        empresa.setAlignment(Element.ALIGN_CENTER);
                        documento.add(empresa);

                        Paragraph nombreReporte = new Paragraph(
                                        "Reporte general de entregas", subtitulo);
                        nombreReporte.setAlignment(Element.ALIGN_CENTER);
                        documento.add(nombreReporte);

                        documento.add(new Paragraph(" "));

                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        documento.add(new Paragraph(
                                        "Fecha de generación: "
                                                        + LocalDateTime.now().format(formatoFecha),
                                        normal));

                        int pendientes = 0;
                        int enCamino = 0;
                        int entregadas = 0;
                        int canceladas = 0;

                        PdfPTable tabla = new PdfPTable(8);
                        tabla.setWidthPercentage(100);

                        tabla.setWidths(new float[] {
                                        0.7f, 0.7f, 1.4f, 2.0f,
                                        1.4f, 1.4f, 1.2f, 1.8f
                        });

                        agregarCelda(tabla, "N.º Entrega", encabezado);
                        agregarCelda(tabla, "N.º Pedido", encabezado);
                        agregarCelda(tabla, "Cliente", encabezado);
                        agregarCelda(tabla, "Dirección", encabezado);
                        agregarCelda(tabla, "Fecha programada", encabezado);
                        agregarCelda(tabla, "Fecha de entrega", encabezado);
                        agregarCelda(tabla, "Estado", encabezado);
                        agregarCelda(tabla, "Responsable", encabezado);

                        for (Entrega entrega : entregas) {

                                if ("PENDIENTE".equalsIgnoreCase(entrega.getEstado())) {
                                        pendientes++;
                                } else if ("EN_CAMINO".equalsIgnoreCase(entrega.getEstado())) {
                                        enCamino++;
                                } else if ("ENTREGADA".equalsIgnoreCase(entrega.getEstado())) {
                                        entregadas++;
                                } else if ("CANCELADA".equalsIgnoreCase(entrega.getEstado())) {
                                        canceladas++;
                                }

                                agregarCelda(tabla, texto(entrega.getId()), normal);

                                agregarCelda(
                                                tabla,
                                                texto(entrega.getPedido().getId()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(entrega.getPedido().getCliente().getNombre()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(entrega.getDireccionEntrega()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                entrega.getFechaProgramada() != null
                                                                ? entrega.getFechaProgramada().format(formatoFecha)
                                                                : "Sin programar",
                                                normal);

                                agregarCelda(
                                                tabla,
                                                entrega.getFechaEntrega() != null
                                                                ? entrega.getFechaEntrega().format(formatoFecha)
                                                                : "Pendiente",
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(entrega.getEstado()),
                                                normal);

                                agregarCelda(
                                                tabla,
                                                texto(entrega.getResponsable()),
                                                normal);
                        }

                        documento.add(tabla);
                        documento.add(new Paragraph(" "));

                        documento.add(new Paragraph(
                                        "Total de entregas: " + entregas.size(), normal));

                        documento.add(new Paragraph(
                                        "Entregas pendientes: " + pendientes, normal));

                        documento.add(new Paragraph(
                                        "Entregas en camino: " + enCamino, normal));

                        documento.add(new Paragraph(
                                        "Entregas realizadas: " + entregadas, normal));

                        documento.add(new Paragraph(
                                        "Entregas canceladas: " + canceladas, normal));

                        documento.add(new Paragraph(" "));

                        Paragraph resumen = new Paragraph(
                                        "Total de entregas completadas: " + entregadas,
                                        subtitulo);
                        documento.add(resumen);

                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException e) {
                        throw new RuntimeException(
                                        "Error al generar el reporte de entregas", e);
                }
        }

        private void agregarCelda(
                        PdfPTable tabla,
                        String contenido,
                        Font fuente) {
                PdfPCell celda = new PdfPCell(new Phrase(contenido, fuente));
                celda.setPadding(5);
                tabla.addCell(celda);
        }

        private boolean enRango(java.time.LocalDateTime f, java.time.LocalDate ini, java.time.LocalDate fin) {
                if (ini == null && fin == null) {
                        return true;
                }
                if (f == null) {
                        return false;
                }
                java.time.LocalDate d = f.toLocalDate();
                return (ini == null || !d.isBefore(ini)) && (fin == null || !d.isAfter(fin));
        }

        private String texto(Object valor) {
                return valor == null ? "-" : valor.toString();
        }
}