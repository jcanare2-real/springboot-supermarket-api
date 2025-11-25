package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.todocodeacademy.PruebaTecSupermercado.dto.ProductoDTO;
import com.todocodeacademy.PruebaTecSupermercado.service.IProductoService;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService){
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(){
        return ResponseEntity.ok(this.productoService.getAll());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid  @RequestBody ProductoDTO productoDTO){
        ProductoDTO creado = this.productoService.create(productoDTO);
        return ResponseEntity.created(URI.create("/api/productos/crear/" + creado.getId())).body(creado);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long idProducto,
                                                  @Valid @RequestBody ProductoDTO productoDTO){
        ProductoDTO updated = this.productoService.update(idProducto, productoDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminar(@PathVariable  Long idProducto){
        this.productoService.delete(idProducto);
        return ResponseEntity.noContent().build();
    }
}
