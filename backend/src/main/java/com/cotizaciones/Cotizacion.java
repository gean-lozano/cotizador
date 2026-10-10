package com.cotizaciones;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, length = 20)
    public String numero;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    public String clienteNombre;

    @Column(name = "cliente_documento", length = 20)
    public String clienteDocumento;

    @Column(name = "cliente_email", length = 150)
    public String clienteEmail;

    @Column(name = "cliente_telefono", length = 30)
    public String clienteTelefono;

    @Column(nullable = false)
    public LocalDate fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal igv;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal total;

    @Column(nullable = false, length = 20)
    public String estado;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<DetalleCotizacion> detalles = new ArrayList<>();
}
