package com.IsilERPSpring.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.IsilERPSpring.entity.DetalleCompra;
import com.IsilERPSpring.entity.OrdenCompra;
import com.IsilERPSpring.repository.DetalleCompraRepository;
import com.IsilERPSpring.repository.OrdenCompraRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class DetalleCompraPDFController {

    @Autowired
    private OrdenCompraRepository ordencompraRepository;

    @Autowired
    private DetalleCompraRepository detallecompraRepository;

    // Mapeo de la URL para la exportación del PDF
    @GetMapping("/exportar-detalle-compra/{id}")
    public ResponseEntity<byte[]> exportarDetalleCompra(@PathVariable int id) {
        System.out.println("ID recibido: " + id);
        // Buscar la orden de compra y sus detalles
        OrdenCompra ordenCompra = ordencompraRepository.findById(id);
        List<DetalleCompra> listaDetalle = detallecompraRepository.findByOrdenCompra(ordenCompra);

        // Crear un ByteArrayOutputStream para almacenar el PDF en memoria
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Crear el documento PDF
            Document document = new Document();
            
            // Establecer márgenes
            document.setMargins(36, 36, 36, 36);
            
            // Crear un escritor de PDF que escribe en el ByteArrayOutputStream
            PdfWriter.getInstance(document, baos);
            document.open(); // Abrir el documento para agregar contenido

            // Definir las fuentes para el contenido
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 20, Font.BOLD, BaseColor.DARK_GRAY);
            Font headerFont = new Font(baseFont, 12, Font.BOLD, BaseColor.WHITE);
            Font contentFont = new Font(baseFont, 12);

            // Agregar un título al PDF
            Paragraph title = new Paragraph("DETALLES DE LA COMPRA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            title.setSpacingBefore(10);
            document.add(title);

            // Agregar la información de la orden de compra
            Paragraph orderInfo = new Paragraph("Orden de compra: " + ordenCompra.getId(), headerFont);
            orderInfo.setSpacingAfter(5);
            document.add(orderInfo);
            
            Paragraph orderDate = new Paragraph("Fecha: " + ordenCompra.getFechaRegistro(), contentFont);
            orderDate.setSpacingAfter(15);
            document.add(orderDate);

            // Agregar una línea divisoria para separar la cabecera
            LineSeparator line = new LineSeparator();
            line.setPercentage(100f);
            document.add(line);
            document.add(Chunk.NEWLINE);

            // Agregar la lista de productos en formato de tabla
            PdfPTable table = new PdfPTable(4); // Definir 4 columnas: Producto, Cantidad, Precio Unitario, Precio Total
            table.setWidthPercentage(100); // Hacer que la tabla ocupe todo el ancho disponible

            // Establecer el encabezado de la tabla con colores de fondo y bordes
            PdfPCell cell = new PdfPCell(new Phrase("Producto", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Cantidad", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio Unitario", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio Total", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(BaseColor.BLACK);
            table.addCell(cell);

            // Llenar la tabla con los detalles de los productos
            for (DetalleCompra detalle : listaDetalle) {
                table.addCell(new Phrase(detalle.getArticulo().getNombre(), contentFont));
                table.addCell(new Phrase(String.valueOf(detalle.getCantidad()), contentFont));
                table.addCell(new Phrase(String.format("S/ %.2f", detalle.getPrecioUnitario()), contentFont));
                table.addCell(new Phrase(String.format("S/ %.2f", detalle.getPrecioTotal()), contentFont));
            }

            // Agregar la tabla al documento
            document.add(table);

            // Agregar el total general de la compra con fondo y formato
            double totalCompra = listaDetalle.stream()
                    .mapToDouble(DetalleCompra::getPrecioTotal)
                    .sum();
            Paragraph total = new Paragraph("Total de la compra: ", new Font(baseFont, 14, Font.BOLD, BaseColor.WHITE));
            Paragraph totalValue = new Paragraph(String.format("S/ %.2f", totalCompra), new Font(baseFont, 16, Font.BOLD, BaseColor.RED));
            total.setAlignment(Element.ALIGN_RIGHT);
            totalValue.setAlignment(Element.ALIGN_RIGHT);
            
            // Crear una celda con fondo para el total
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.addCell(createCell(total, BaseColor.DARK_GRAY));
            totalTable.addCell(createCell(totalValue, BaseColor.LIGHT_GRAY));
            document.add(totalTable);

            // Cerrar el documento
            document.close();

            // Establecer los encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=detalle_compra_" + ordenCompra.getId() + ".pdf");

            // Devolver el PDF como respuesta
            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método auxiliar para crear celdas de la tabla con un fondo específico
    private PdfPCell createCell(Paragraph paragraph, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setPadding(10);
        return cell;
    }
}
