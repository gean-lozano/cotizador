package com.cotizaciones;

import java.util.List;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    private final CotizacionService service;

    public CotizacionController(CotizacionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cotizacion> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cotizacion crear(@RequestBody CotizacionRequest req) {
        return service.crear(req);
    }

    @PatchMapping("/{id}/estado")
    public Cotizacion estado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.cambiarEstado(id, body.get("estado"));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] bytes = service.generarPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=cotizacion-" + id + ".pdf")
                .body(bytes);
    }
}
