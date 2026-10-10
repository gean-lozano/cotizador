package com.cotizaciones;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);

    public byte[] generar(Cotizacion c) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("COTIZACIÓN", TITULO));
            doc.add(new Paragraph("N° " + c.numero + "    Fecha: " + c.fecha, NORMAL));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Cliente: " + c.clienteNombre, NEGRITA));
            if (notBlank(c.clienteDocumento)) doc.add(new Paragraph("RUC/DNI: " + c.clienteDocumento, NORMAL));
            if (notBlank(c.clienteEmail)) doc.add(new Paragraph("Email: " + c.clienteEmail, NORMAL));
            if (notBlank(c.clienteTelefono)) doc.add(new Paragraph("Teléfono: " + c.clienteTelefono, NORMAL));

            PdfPTable t = new PdfPTable(new float[] {5f, 1.3f, 2f, 2f});
            t.setWidthPercentage(100);
            t.setSpacingBefore(14);
            for (String h : new String[] {"Descripción", "Cant.", "P. unit.", "Subtotal"}) {
                PdfPCell cell = celda(h, NEGRITA, Element.ALIGN_LEFT);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                t.addCell(cell);
            }
            for (DetalleCotizacion d : c.detalles) {
                t.addCell(celda(d.concepto, NORMAL, Element.ALIGN_LEFT));
                t.addCell(celda(String.valueOf(d.cantidad), NORMAL, Element.ALIGN_CENTER));
                t.addCell(celda(money(d.precioUnitario), NORMAL, Element.ALIGN_RIGHT));
                t.addCell(celda(money(d.subtotal), NORMAL, Element.ALIGN_RIGHT));
            }
            doc.add(t);

            PdfPTable tot = new PdfPTable(new float[] {2f, 2f});
            tot.setWidthPercentage(45);
            tot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tot.setSpacingBefore(10);
            fila(tot, "Subtotal", money(c.subtotal), NORMAL);
            fila(tot, "IGV (18%)", money(c.igv), NORMAL);
            fila(tot, "TOTAL", money(c.total), NEGRITA);
            doc.add(tot);

            doc.close();
            return out.toByteArray();
        } catch (java.io.IOException | DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF", e);
        }
    }

    private static void fila(PdfPTable t, String a, String b, Font f) {
        t.addCell(celda(a, f, Element.ALIGN_LEFT));
        t.addCell(celda(b, f, Element.ALIGN_RIGHT));
    }

    private static PdfPCell celda(String txt, Font f, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(txt, f));
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private static String money(BigDecimal v) {
        return String.format(Locale.US, "S/ %,.2f", v);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
