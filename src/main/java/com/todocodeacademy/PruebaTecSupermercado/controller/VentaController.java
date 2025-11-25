package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.todocodeacademy.PruebaTecSupermercado.dto.VentaDTO;
import com.todocodeacademy.PruebaTecSupermercado.service.IVentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final IVentaService ventasService;

    public VentaController(IVentaService ventasService){
        this.ventasService = ventasService;
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar(){
        return ResponseEntity.ok(this.ventasService.getAll());
    }

    @PostMapping
    public ResponseEntity<VentaDTO> crear(@Valid  @RequestBody VentaDTO ventaDTO){
        VentaDTO created = this.ventasService.create(ventaDTO);

        return ResponseEntity.created(URI.create("/api/ventas/crear/" + created.getId())).body(created);
    }

    @PutMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> actualizar(@PathVariable Long idVenta,
                                               @Valid @RequestBody VentaDTO ventaDTO){
        VentaDTO updated = this.ventasService.update(idVenta, ventaDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idVenta){
        this.ventasService.delete(idVenta);
        return ResponseEntity.noContent().build();
    }
}
