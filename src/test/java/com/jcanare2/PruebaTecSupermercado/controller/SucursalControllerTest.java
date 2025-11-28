package com.todocodeacademy.PruebaTecSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todocodeacademy.PruebaTecSupermercado.dto.SucursalDTO;
import com.todocodeacademy.PruebaTecSupermercado.exception.NotFoundException;
import com.todocodeacademy.PruebaTecSupermercado.service.ISucursalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(SucursalController.class)
@DisplayName("SucursalController - Tests Unitarios")
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISucursalService sucursalService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE ====================

    @Nested
    @DisplayName("POST /api/sucursales - Crear Sucursal")
    class CrearSucursalTests {

        @Test
        @DisplayName("Debe crear sucursal exitosamente y retornar 201 con Location header")
        void testCrear_DebeCrearSucursalExitosamente() throws Exception {
            // Given
            SucursalDTO nuevaSucursal = SucursalDTO.builder()
                    .nombre("Sucursal Centro")
                    .direccion("Av. Principal 123")
                    .build();

            SucursalDTO sucursalCreada = SucursalDTO.builder()
                    .id(10L)
                    .nombre("Sucursal Centro")
                    .direccion("Av. Principal 123")
                    .build();

            when(sucursalService.create(any(SucursalDTO.class))).thenReturn(sucursalCreada);

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevaSucursal)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/sucursales/crear/10"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.nombre").value("Sucursal Centro"))
                    .andExpect(jsonPath("$.direccion").value("Av. Principal 123"));

            // Verify service call
            ArgumentCaptor<SucursalDTO> captor = ArgumentCaptor.forClass(SucursalDTO.class);
            verify(sucursalService, times(1)).create(captor.capture());
            SucursalDTO captured = captor.getValue();
            assertThat(captured.getNombre()).isEqualTo("Sucursal Centro");
            assertThat(captured.getDireccion()).isEqualTo("Av. Principal 123");
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el nombre está vacío")
        void testCrear_DebeRetornar400ConNombreVacio() throws Exception {
            // Given
            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre("")                 // @NotBlank
                    .direccion("Av. Siempre Viva 742")
                    .build();

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists());

            verify(sucursalService, never()).create(any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando la dirección está vacía")
        void testCrear_DebeRetornar400ConDireccionVacia() throws Exception {
            // Given
            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre("Sucursal Norte")
                    .direccion("")              // @NotBlank
                    .build();

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.direccion").exists());

            verify(sucursalService, never()).create(any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 con múltiples errores de validación")
        void testCrear_DebeRetornar400ConMultiplesErrores() throws Exception {
            // Given
            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre("")     // inválido
                    .direccion("")  // inválido
                    .build();

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                    .andExpect(jsonPath("$.fieldErrors.direccion").exists());

            verify(sucursalService, never()).create(any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el nombre excede 100 caracteres")
        void testCrear_DebeRetornar400ConNombreLargo() throws Exception {
            // Given
            String nombreLargo = "A".repeat(101);
            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre(nombreLargo)
                    .direccion("Dirección válida")
                    .build();

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists());

            verify(sucursalService, never()).create(any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando la dirección excede 200 caracteres")
        void testCrear_DebeRetornar400ConDireccionLarga() throws Exception {
            // Given
            String direccionLarga = "B".repeat(201);
            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre("Sucursal Oeste")
                    .direccion(direccionLarga)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.direccion").exists());

            verify(sucursalService, never()).create(any(SucursalDTO.class));
        }
    }

    // ==================== UPDATE ====================

    @Nested
    @DisplayName("PUT /api/sucursales/{idSucursal} - Actualizar Sucursal")
    class ActualizarSucursalTests {

        @Test
        @DisplayName("Debe actualizar sucursal exitosamente y retornar 200")
        void testActualizar_DebeActualizarSucursalExitosamente() throws Exception {
            // Given
            Long idSucursal = 1L;

            SucursalDTO sucursalActualizada = SucursalDTO.builder()
                    .nombre("Sucursal Centro Actualizada")
                    .direccion("Calle Nueva 456")
                    .build();

            SucursalDTO sucursalResponse = SucursalDTO.builder()
                    .id(idSucursal)
                    .nombre("Sucursal Centro Actualizada")
                    .direccion("Calle Nueva 456")
                    .build();

            when(sucursalService.update(eq(idSucursal), any(SucursalDTO.class)))
                    .thenReturn(sucursalResponse);

            // When & Then
            mockMvc.perform(put("/api/sucursales/{idSucursal}", idSucursal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sucursalActualizada)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(idSucursal))
                    .andExpect(jsonPath("$.nombre").value("Sucursal Centro Actualizada"))
                    .andExpect(jsonPath("$.direccion").value("Calle Nueva 456"));

            verify(sucursalService, times(1)).update(eq(idSucursal), any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 con datos inválidos")
        void testActualizar_DebeRetornar400ConDatosInvalidos() throws Exception {
            // Given
            Long idSucursal = 1L;

            SucursalDTO invalida = SucursalDTO.builder()
                    .nombre("")
                    .direccion("")
                    .build();

            // When & Then
            mockMvc.perform(put("/api/sucursales/{idSucursal}", idSucursal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                    .andExpect(jsonPath("$.fieldErrors.direccion").exists());

            verify(sucursalService, never()).update(anyLong(), any(SucursalDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la sucursal no existe")
        void testActualizar_DebeRetornar404CuandoNoExiste() throws Exception {
            // Given
            Long idSucursal = 99L;

            SucursalDTO dto = SucursalDTO.builder()
                    .nombre("Sucursal Fantasma")
                    .direccion("Calle Falsa 123")
                    .build();

            when(sucursalService.update(eq(idSucursal), any(SucursalDTO.class)))
                    .thenThrow(new NotFoundException("Sucursal no encontrada"));

            // When & Then
            mockMvc.perform(put("/api/sucursales/{idSucursal}", idSucursal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Sucursal no encontrada"));
        }
    }

    // ==================== READ ====================

    @Nested
    @DisplayName("GET /api/sucursales - Listar Sucursales")
    class ListarSucursalesTests {

        @Test
        @DisplayName("Debe listar todas las sucursales exitosamente")
        void testListar_DebeListarTodasLasSucursales() throws Exception {
            // Given
            List<SucursalDTO> sucursales = Arrays.asList(
                    SucursalDTO.builder()
                            .id(1L)
                            .nombre("Sucursal Centro")
                            .direccion("Av. Principal 123")
                            .build(),
                    SucursalDTO.builder()
                            .id(2L)
                            .nombre("Sucursal Norte")
                            .direccion("Ruta 5 km 20")
                            .build()
            );

            when(sucursalService.getAll()).thenReturn(sucursales);

            // When & Then
            mockMvc.perform(get("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Sucursal Centro"))
                    .andExpect(jsonPath("$[0].direccion").value("Av. Principal 123"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].nombre").value("Sucursal Norte"));
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay sucursales")
        void testListar_DebeRetornarListaVacia() throws Exception {
            // Given
            when(sucursalService.getAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/sucursales")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(sucursalService, times(1)).getAll();
        }
    }

    // ==================== DELETE ====================

    @Nested
    @DisplayName("DELETE /api/sucursales/{idSucursal} - Eliminar Sucursal")
    class EliminarSucursalTests {

        @Test
        @DisplayName("Debe eliminar sucursal exitosamente y retornar 204")
        void testEliminar_DebeEliminarSucursalExitosamente() throws Exception {
            // Given
            Long idSucursal = 1L;
            doNothing().when(sucursalService).delete(idSucursal);

            // When & Then
            mockMvc.perform(delete("/api/sucursales/{idSucursal}", idSucursal))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(sucursalService, times(1)).delete(idSucursal);
        }

        @Test
        @DisplayName("Debe invocar el servicio de eliminación con el ID correcto")
        void testEliminar_DebeInvocarServicioConIdCorrecto() throws Exception {
            // Given
            Long idSucursal = 42L;
            doNothing().when(sucursalService).delete(idSucursal);

            // When & Then
            mockMvc.perform(delete("/api/sucursales/{idSucursal}", idSucursal))
                    .andExpect(status().isNoContent());

            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(sucursalService, times(1)).delete(captor.capture());
            assertThat(captor.getValue()).isEqualTo(42L);
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la sucursal no existe al eliminar")
        void testEliminar_DebeRetornar404CuandoNoExiste() throws Exception {
            // Given
            Long idSucursal = 99L;
            doThrow(new NotFoundException("Sucursal no encontrada"))
                    .when(sucursalService).delete(idSucursal);

            // When & Then
            mockMvc.perform(delete("/api/sucursales/{idSucursal}", idSucursal))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Sucursal no encontrada"));
        }
    }
}