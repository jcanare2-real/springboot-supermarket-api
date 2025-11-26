package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.todocodeacademy.PruebaTecSupermercado.dto.VentaDTO;
import com.todocodeacademy.PruebaTecSupermercado.service.IVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Gestión de transacciones y registros de ventas.")
public class VentaController {

    private final IVentaService ventasService;

    public VentaController(IVentaService ventasService){
        this.ventasService = ventasService;
    }

    @Operation(
            summary = "Obtener todas las ventas",
            description = "Devuelve una lista con todas las transacciones de ventas registradas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de ventas recuperada con éxito.",
                            content = @Content(schema = @Schema(implementation = VentaDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar(){
        return ResponseEntity.ok(this.ventasService.getAll());
    }

    @Operation(
            summary = "Registrar una nueva venta",
            description = "Crea una nueva transacción de venta, incluyendo productos, cliente y sucursal.",
            requestBody = @RequestBody(
                    description = "Datos de la venta a registrar (requiere ítems, cliente, etc.)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VentaDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Venta registrada y creada exitosamente.",
                            content = @Content(schema = @Schema(implementation = VentaDTO.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (ej. datos de ítems de venta incorrectos).")
            }
    )
    @PostMapping
    public ResponseEntity<VentaDTO> crear(@Valid  @RequestBody VentaDTO ventaDTO){
        VentaDTO created = this.ventasService.create(ventaDTO);

        return ResponseEntity.created(URI.create("/api/ventas/crear/" + created.getId())).body(created);
    }

    @Operation(
            summary = "Actualizar venta por ID",
            description = "Modifica los detalles de una venta existente (ej. corrección de datos).",
            parameters = {
                    @Parameter(
                            name = "idVenta",
                            description = "Identificador único de la venta a actualizar.",
                            required = true,
                            example = "45")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Venta actualizada con éxito.",
                            content = @Content(schema = @Schema(implementation = VentaDTO.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Venta no encontrada."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de solicitud inválidos.")
            }
    )
    @PutMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> actualizar(@PathVariable Long idVenta,
                                               @Valid @RequestBody VentaDTO ventaDTO){
        VentaDTO updated = this.ventasService.update(idVenta, ventaDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar venta por ID",
            description = "Elimina permanentemente una transacción de venta del sistema (usar con precaución).",
            parameters = {
                    @Parameter(
                            name = "idVenta",
                            description = "Identificador único de la venta a eliminar.",
                            required = true,
                            example = "50")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Venta eliminada exitosamente (No Content)."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Venta no encontrada.")
            }
    )
    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idVenta){
        this.ventasService.delete(idVenta);
        return ResponseEntity.noContent().build();
    }
}
