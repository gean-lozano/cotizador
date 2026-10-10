package com.cotizaciones;

import java.math.BigDecimal;
import java.util.List;

public record CotizacionRequest(
        String clienteNombre,
        String clienteDocumento,
        String clienteEmail,
        String clienteTelefono,
        List<Item> items) {

    public record Item(Long productoId, String concepto, Integer cantidad, BigDecimal precioUnitario) {}
}
