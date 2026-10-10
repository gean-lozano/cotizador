package com.cotizaciones;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrueOrderByNombre();
    List<Producto> findAllByOrderByNombre();
}
