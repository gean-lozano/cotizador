package com.cotizaciones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_cotizacion")
public class DetalleCotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cotizacion_id")
    public Cotizacion cotizacion;

    @Column(name = "producto_id")
    public Long productoId;

    @Column(nullable = false)
    public String concepto;

    @Column(nullable = false)
    public Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    public BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal subtotal;
}
