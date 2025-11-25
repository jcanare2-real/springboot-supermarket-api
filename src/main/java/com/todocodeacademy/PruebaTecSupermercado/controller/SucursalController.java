package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.todocodeacademy.PruebaTecSupermercado.dto.SucursalDTO;
import com.todocodeacademy.PruebaTecSupermercado.service.ISucursalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final ISucursalService sucursalService;

    public SucursalController(ISucursalService sucursalService){
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> listar(){
        return ResponseEntity.ok(this.sucursalService.getAll());
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO sucursalDTO){

        SucursalDTO created = this.sucursalService.create(sucursalDTO);

        return ResponseEntity.created(URI.create("/api/sucursales/crear/" + created.getId())).body(created);
    }

    @PutMapping("/{idSucursal}")
    public ResponseEntity<SucursalDTO> actualizar(@PathVariable Long idSucursal,
                                                  @Valid @RequestBody SucursalDTO sucursalDTO){
        SucursalDTO updated = this.sucursalService.update(idSucursal, sucursalDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{idSucursal}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idSucursal){
        this.sucursalService.delete(idSucursal);
        return ResponseEntity.noContent().build();
    }

}
