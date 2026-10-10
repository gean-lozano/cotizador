package com.cotizaciones;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoRepository repo;

    public ProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Producto> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return soloActivos ? repo.findByActivoTrueOrderByNombre() : repo.findAllByOrderByNombre();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody Producto p) {
        validar(p);
        p.id = null;
        p.activo = true;
        return repo.save(p);
    }

    @PutMapping("/{id}")
    public Producto editar(@PathVariable Long id, @RequestBody Producto datos) {
        validar(datos);
        Producto p = buscar(id);
        p.nombre = datos.nombre.trim();
        p.descripcion = datos.descripcion;
        p.precio = datos.precio;
        return repo.save(p);
    }

    @PatchMapping("/{id}/activo")
    public Producto alternarActivo(@PathVariable Long id) {
        Producto p = buscar(id);
        p.activo = !p.activo;
        return repo.save(p);
    }

    private Producto buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private void validar(Producto p) {
        if (p.nombre == null || p.nombre.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        if (p.precio == null || p.precio.signum() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Precio inválido");
    }
}
