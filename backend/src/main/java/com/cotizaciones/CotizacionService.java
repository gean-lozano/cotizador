package com.cotizaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CotizacionService {
    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "APROBADA", "RECHAZADA");

    private final CotizacionRepository repo;
    private final ProductoRepository productos;
    private final PdfService pdf;
    private final BigDecimal igvRate;

    public CotizacionService(CotizacionRepository repo, ProductoRepository productos, PdfService pdf,
                             @Value("${app.igv}") BigDecimal igvRate) {
        this.repo = repo;
        this.productos = productos;
        this.pdf = pdf;
        this.igvRate = igvRate;
    }

    @Transactional(readOnly = true)
    public List<Cotizacion> listar() {
        return repo.findAllByOrderByIdDesc();
    }

    @Transactional
    public Cotizacion crear(CotizacionRequest req) {
        if (req.clienteNombre() == null || req.clienteNombre().isBlank())
            throw bad("El nombre del cliente es obligatorio");
        if (req.items() == null || req.items().isEmpty())
            throw bad("Agrega al menos un ítem");

        Cotizacion c = new Cotizacion();
        c.clienteNombre = req.clienteNombre().trim();
        c.clienteDocumento = req.clienteDocumento();
        c.clienteEmail = req.clienteEmail();
        c.clienteTelefono = req.clienteTelefono();
        c.fecha = LocalDate.now();
        c.estado = "PENDIENTE";

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CotizacionRequest.Item it : req.items()) {
            if (it.cantidad() == null || it.cantidad() <= 0) throw bad("La cantidad debe ser mayor a 0");
            if (it.precioUnitario() == null || it.precioUnitario().signum() < 0) throw bad("Precio inválido");

            String concepto = it.concepto();
            if (it.productoId() != null) {
                Producto p = productos.findById(it.productoId())
                        .orElseThrow(() -> bad("Producto no encontrado: " + it.productoId()));
                if (concepto == null || concepto.isBlank()) concepto = p.nombre;
            }
            if (concepto == null || concepto.isBlank()) throw bad("Cada ítem necesita una descripción");

            DetalleCotizacion d = new DetalleCotizacion();
            d.cotizacion = c;
            d.productoId = it.productoId();
            d.concepto = concepto;
            d.cantidad = it.cantidad();
            d.precioUnitario = it.precioUnitario().setScale(2, RoundingMode.HALF_UP);
            d.subtotal = d.precioUnitario.multiply(BigDecimal.valueOf(d.cantidad));
            subtotal = subtotal.add(d.subtotal);
            c.detalles.add(d);
        }
        c.subtotal = subtotal;
        c.igv = subtotal.multiply(igvRate).setScale(2, RoundingMode.HALF_UP);
        c.total = subtotal.add(c.igv);

        c = repo.save(c);
        c.numero = "COT-%d-%05d".formatted(c.fecha.getYear(), c.id);
        return repo.save(c);
    }

    @Transactional
    public Cotizacion cambiarEstado(Long id, String estado) {
        if (estado == null || !ESTADOS.contains(estado)) throw bad("Estado inválido");
        Cotizacion c = buscar(id);
        c.estado = estado;
        return c;
    }

    @Transactional(readOnly = true)
    public byte[] generarPdf(Long id) {
        return pdf.generar(buscar(id));
    }

    private Cotizacion buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cotización no encontrada"));
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
