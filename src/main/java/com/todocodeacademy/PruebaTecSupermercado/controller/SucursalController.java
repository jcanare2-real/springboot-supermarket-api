package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.todocodeacademy.PruebaTecSupermercado.dto.SucursalDTO;
import com.todocodeacademy.PruebaTecSupermercado.service.ISucursalService;
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
@RequestMapping("/api/sucursales")
@Tag(name = "Sucursales", description = "Gestión de las sucursales del supermercado.")
public class SucursalController {

    private final ISucursalService sucursalService;

    public SucursalController(ISucursalService sucursalService){
        this.sucursalService = sucursalService;
    }

    @Operation(
            summary = "Obtener todas las sucursales",
            description = "Devuelve la lista completa de todas las sucursales registradas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de sucursales recuperada con éxito.",
                            content = @Content(schema = @Schema(implementation = SucursalDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<SucursalDTO>> listar(){
        return ResponseEntity.ok(this.sucursalService.getAll());
    }

    @Operation(
            summary = "Crear una nueva sucursal",
            description = "Registra una nueva sucursal con datos de ubicación y contacto.",
            requestBody = @RequestBody(
                    description = "Datos de la sucursal a crear (ID no requerido)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SucursalDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Sucursal creada exitosamente, devuelve el objeto creado.",
                            content = @Content(schema = @Schema(implementation = SucursalDTO.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (errores de validación o datos incorrectos).")
            }
    )
    @PostMapping
    public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO sucursalDTO){

        SucursalDTO created = this.sucursalService.create(sucursalDTO);

        return ResponseEntity.created(URI.create("/api/sucursales/crear/" + created.getId())).body(created);
    }

    @Operation(
            summary = "Actualizar sucursal por ID",
            description = "Modifica todos los campos de una sucursal existente usando su identificador.",
            parameters = {
                    @Parameter(
                            name = "idSucursal",
                            description = "Identificador único de la sucursal a actualizar.",
                            required = true,
                            example = "5")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sucursal actualizada con éxito.",
                            content = @Content(schema = @Schema(implementation = SucursalDTO.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Sucursal no encontrada."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de solicitud inválidos.")
            }
    )
    @PutMapping("/{idSucursal}")
    public ResponseEntity<SucursalDTO> actualizar(@PathVariable Long idSucursal,
                                                  @Valid @RequestBody SucursalDTO sucursalDTO){
        SucursalDTO updated = this.sucursalService.update(idSucursal, sucursalDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar sucursal por ID",
            description = "Elimina permanentemente una sucursal del sistema.",
            parameters = {
                    @Parameter(
                            name = "idSucursal",
                            description = "Identificador único de la sucursal a eliminar.",
                            required = true,
                            example = "12")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Sucursal eliminada exitosamente (No Content)."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Sucursal no encontrada.")
            }
    )
    @DeleteMapping("/{idSucursal}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idSucursal){
        this.sucursalService.delete(idSucursal);
        return ResponseEntity.noContent().build();
    }

}
