package com.cotizaciones;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 150)
    public String nombre;

    @Column(length = 500)
    public String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal precio;

    @Column(nullable = false)
    public boolean activo = true;
}
