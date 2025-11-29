package com.jcanare2.PruebaTecSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcanare2.PruebaTecSupermercado.dto.DetalleVentaDTO;
import com.jcanare2.PruebaTecSupermercado.dto.VentaDTO;
import com.jcanare2.PruebaTecSupermercado.enums.EstadoEnum;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.service.IVentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
@DisplayName("VentaController - Tests Unitarios")
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IVentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE ====================

    @Nested
    @DisplayName("POST /api/ventas - Crear Venta")
    class CrearVentaTests {

        @Test
        @DisplayName("Debe crear venta exitosamente y retornar 201 con Location header")
        void testCrear_DebeCrearVentaExitosamente() throws Exception {
            // Given
            VentaDTO nuevaVenta = VentaDTO.builder()
                    .fecha(LocalDate.of(2025, 1, 1))
                    .estado(EstadoEnum.REGISTRADA)
                    .idSucursal(1L)
                    .detalle(List.of(
                            DetalleVentaDTO.builder()
                                    .nombreProd("Granos")
                                    .cantProd(2)
                                    .precio(100.0)
                                    .subTotal(200.0)
                                    .build()
                    ))
                    .total(200.0)
                    .build();

            VentaDTO ventaCreada = VentaDTO.builder()
                    .id(5L)
                    .fecha(LocalDate.of(2025, 1, 1))
                    .estado(EstadoEnum.REGISTRADA)
                    .idSucursal(1L)
                    .detalle(nuevaVenta.getDetalle())
                    .total(200.0)
                    .build();

            when(ventaService.create(any(VentaDTO.class))).thenReturn(ventaCreada);

            // When & Then
            mockMvc.perform(post("/api/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevaVenta)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/ventas/crear/5"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.idSucursal").value(1))
                    .andExpect(jsonPath("$.total").value(200.0));

            // Verificar que se pasa correctamente al servicio
            ArgumentCaptor<VentaDTO> captor = ArgumentCaptor.forClass(VentaDTO.class);
            verify(ventaService, times(1)).create(captor.capture());
            VentaDTO capturada = captor.getValue();
            assertThat(capturada.getId()).isNull(); // normalmente no se envía id al crear
            assertThat(capturada.getIdSucursal()).isEqualTo(1L);
            assertThat(capturada.getDetalle()).hasSize(1);
            assertThat(capturada.getTotal()).isEqualTo(200.0);
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el body es inválido (ej. falta sucursal o detalle)")
        void testCrear_DebeRetornar400ConDatosInvalidos() throws Exception {
            // IMPORTANTE: hoy tu VentaDTO NO tiene anotaciones @NotNull/@NotEmpty.
            // Este test solo tendrá sentido cuando las añadas (ver sugerencias más abajo).
            VentaDTO invalida = VentaDTO.builder()
                    .fecha(null)
                    .estado(null)
                    .idSucursal(null)
                    .detalle(Collections.emptyList())
                    .total(null)
                    .build();

            mockMvc.perform(post("/api/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"));

            verify(ventaService, never()).create(any(VentaDTO.class));
        }
    }

    // ==================== UPDATE ====================

    @Nested
    @DisplayName("PUT /api/ventas/{idVenta} - Actualizar Venta")
    class ActualizarVentaTests {

        @Test
        @DisplayName("Debe actualizar venta exitosamente y retornar 200")
        void testActualizar_DebeActualizarVentaExitosamente() throws Exception {
            // Given
            Long idVenta = 3L;

            VentaDTO ventaActualizada = VentaDTO.builder()
                    .fecha(LocalDate.of(2025, 1, 2))
                    .estado(EstadoEnum.ANULADA)
                    .idSucursal(2L)
                    .detalle(List.of(
                            DetalleVentaDTO.builder()
                                    .nombreProd("Granos")
                                    .cantProd(1)
                                    .precio(50.0)
                                    .subTotal(50.0)
                                    .build()
                    ))
                    .total(50.0)
                    .build();

            VentaDTO ventaResponse = VentaDTO.builder()
                    .id(idVenta)
                    .fecha(ventaActualizada.getFecha())
                    .estado(ventaActualizada.getEstado())
                    .idSucursal(ventaActualizada.getIdSucursal())
                    .detalle(ventaActualizada.getDetalle())
                    .total(ventaActualizada.getTotal())
                    .build();

            when(ventaService.update(eq(idVenta), any(VentaDTO.class))).thenReturn(ventaResponse);

            // When & Then
            mockMvc.perform(put("/api/ventas/{idVenta}", idVenta)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ventaActualizada)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(idVenta))
                    .andExpect(jsonPath("$.estado").value("ANULADA"))
                    .andExpect(jsonPath("$.total").value(50.0));

            verify(ventaService, times(1)).update(eq(idVenta), any(VentaDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la venta no existe")
        void testActualizar_DebeRetornar404CuandoNoExiste() throws Exception {
            // Given
            Long idVenta = 99L;

            VentaDTO dto = VentaDTO.builder()
                    .fecha(LocalDate.now())
                    .estado(EstadoEnum.REGISTRADA)
                    .idSucursal(1L)
                    .detalle(List.of(
                            DetalleVentaDTO.builder()
                                    .nombreProd("Granos")
                                    .cantProd(1)
                                    .precio(50.0)
                                    .subTotal(50.0)
                                    .build()
                    ))
                    .total(0.0)
                    .build();

            when(ventaService.update(eq(idVenta), any(VentaDTO.class)))
                    .thenThrow(new NotFoundException("Venta no encontrada"));

            // When & Then
            mockMvc.perform(put("/api/ventas/{idVenta}", idVenta)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Venta no encontrada"));
        }

        @Test
        @DisplayName("Debe retornar 400 con datos inválidos en actualización")
        void testActualizar_DebeRetornar400ConDatosInvalidos() throws Exception {
            // Igual que antes: este test asume que pondrás anotaciones de validación.
            Long idVenta = 1L;
            VentaDTO invalida = VentaDTO.builder()
                    .fecha(null)
                    .estado(null)
                    .idSucursal(null)
                    .detalle(null)
                    .total(null)
                    .build();

            mockMvc.perform(put("/api/ventas/{idVenta}", idVenta)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"));

            verify(ventaService, never()).update(anyLong(), any(VentaDTO.class));
        }
    }

    // ==================== READ ====================

    @Nested
    @DisplayName("GET /api/ventas - Listar Ventas")
    class ListarVentasTests {

        @Test
        @DisplayName("Debe listar todas las ventas exitosamente")
        void testListar_DebeListarTodasLasVentas() throws Exception {
            // Given
            List<VentaDTO> ventas = Arrays.asList(
                    VentaDTO.builder()
                            .id(1L)
                            .fecha(LocalDate.of(2025, 1, 1))
                            .estado(EstadoEnum.REGISTRADA)
                            .idSucursal(1L)
                            .total(100.0)
                            .build(),
                    VentaDTO.builder()
                            .id(2L)
                            .fecha(LocalDate.of(2025, 1, 2))
                            .estado(EstadoEnum.ANULADA)
                            .idSucursal(2L)
                            .total(50.0)
                            .build()
            );

            when(ventaService.getAll()).thenReturn(ventas);

            // When & Then
            mockMvc.perform(get("/api/ventas")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].estado").value("REGISTRADA"))
                    .andExpect(jsonPath("$[0].total").value(100.0))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].estado").value("ANULADA"))
                    .andExpect(jsonPath("$[1].total").value(50.0));

            verify(ventaService, times(1)).getAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay ventas")
        void testListar_DebeRetornarListaVacia() throws Exception {
            // Given
            when(ventaService.getAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/ventas")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(ventaService, times(1)).getAll();
        }
    }

    // ==================== DELETE ====================

    @Nested
    @DisplayName("DELETE /api/ventas/{idVenta} - Eliminar Venta")
    class EliminarVentaTests {

        @Test
        @DisplayName("Debe eliminar venta exitosamente y retornar 204")
        void testEliminar_DebeEliminarVentaExitosamente() throws Exception {
            // Given
            Long idVenta = 1L;
            doNothing().when(ventaService).delete(idVenta);

            // When & Then
            mockMvc.perform(delete("/api/ventas/{idVenta}", idVenta))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(ventaService, times(1)).delete(idVenta);
        }

        @Test
        @DisplayName("Debe invocar el servicio de eliminación con el ID correcto")
        void testEliminar_DebeInvocarServicioConIdCorrecto() throws Exception {
            // Given
            Long idVenta = 77L;
            doNothing().when(ventaService).delete(idVenta);

            // When & Then
            mockMvc.perform(delete("/api/ventas/{idVenta}", idVenta))
                    .andExpect(status().isNoContent());

            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(ventaService, times(1)).delete(captor.capture());
            assertThat(captor.getValue()).isEqualTo(77L);
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la venta no existe al eliminar")
        void testEliminar_DebeRetornar404CuandoNoExiste() throws Exception {
            // Given
            Long idVenta = 99L;
            doThrow(new NotFoundException("Venta no encontrada"))
                    .when(ventaService).delete(idVenta);

            // When & Then
            mockMvc.perform(delete("/api/ventas/{idVenta}", idVenta))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Venta no encontrada"));
        }
    }
}