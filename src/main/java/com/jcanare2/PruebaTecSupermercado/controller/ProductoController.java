package com.jcanare2.PruebaTecSupermercado.controller;

import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.service.IProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de inventario del supermercado")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService){
        this.productoService = productoService;
    }

    @Operation(
            summary = "Obtener todos los productos",
            description = "Devuelve una lista con todos los productos disponibles en el inventario.")
    @ApiResponse(responseCode = "200", description = "Lista de productos recuperada con éxito")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(){
        return ResponseEntity.ok(this.productoService.getAll());
    }

    @Operation(
            summary = "Crear un nuevo producto",
            description = "Registra un nuevo producto en el inventario, requiere datos válidos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear (ID no requerido)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Producto creado exitosamente",
                            content = @Content(schema = @Schema(implementation = ProductoDTO.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (Errores de validación @Valid)")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid  @RequestBody ProductoDTO productoDTO){
        ProductoDTO creado = this.productoService.create(productoDTO);
        return ResponseEntity.created(URI.create("/api/productos/crear/" + creado.getId())).body(creado);
    }

    @Operation(
            summary = "Actualizar un producto existente",
            description = "Modifica los datos de un producto por su ID.",
            parameters = {
                    @Parameter(name = "idProducto", description = "ID del producto a actualizar", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long idProducto,
                                                  @Valid @RequestBody ProductoDTO productoDTO){
        ProductoDTO updated = this.productoService.update(idProducto, productoDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina permanentemente un producto del inventario usando su ID.",
            parameters = {
                    @Parameter(name = "idProducto", description = "ID del producto a eliminar", required = true, example = "105")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Producto eliminado (No Content)"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminar(@PathVariable  Long idProducto){
        this.productoService.delete(idProducto);
        return ResponseEntity.noContent().build();
    }
}
