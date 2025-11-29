package com.jcanare2.PruebaTecSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.service.IProductoService;
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
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@DisplayName("ProductoController - Tests Unitarios")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE ====================

    @Nested
    @DisplayName("POST /api/productos - Crear Producto")
    class CrearProductoTests {

        @Test
        @DisplayName("Debe crear producto exitosamente y retornar 201 con Location header")
        void testCrear_DebeCrearProductoExitosamente() throws Exception {
            // Given
            ProductoDTO productoNuevo = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(120.00)
                    .cantidad(100)
                    .build();

            ProductoDTO productoCreado = ProductoDTO.builder()
                    .id(3L)
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(120.00)
                    .cantidad(100)
                    .build();

            when(productoService.create(any(ProductoDTO.class))).thenReturn(productoCreado);

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoNuevo)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/productos/crear/3"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.nombre").value("Arroz Integral"))
                    .andExpect(jsonPath("$.categoria").value("Granos"))
                    .andExpect(jsonPath("$.precio").value(120.00))
                    .andExpect(jsonPath("$.cantidad").value(100));

            // Verify
            ArgumentCaptor<ProductoDTO> captor = ArgumentCaptor.forClass(ProductoDTO.class);
            verify(productoService, times(1)).create(captor.capture());

            ProductoDTO captured = captor.getValue();
            assertThat(captured.getNombre()).isEqualTo("Arroz Integral");
            assertThat(captured.getCategoria()).isEqualTo("Granos");
            assertThat(captured.getPrecio()).isEqualTo(120.00);
            assertThat(captured.getCantidad()).isEqualTo(100);
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el nombre está vacío")
        void testCrear_DebeRetornar400ConNombreVacio() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("") // Inválido
                    .categoria("Granos")
                    .precio(120.00)
                    .cantidad(100)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Error de validación"))
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando la categoría está vacía")
        void testCrear_DebeRetornar400ConCategoriaVacia() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("") // Inválido
                    .precio(120.00)
                    .cantidad(100)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.categoria").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el precio es null")
        void testCrear_DebeRetornar400ConPrecioNull() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(null) // Inválido
                    .cantidad(100)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.precio").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el precio es negativo")
        void testCrear_DebeRetornar400ConPrecioNegativo() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(-10.0) // Inválido
                    .cantidad(100)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.precio").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando la cantidad es null")
        void testCrear_DebeRetornar400ConCantidadNull() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(120.0)
                    .cantidad(null) // Inválido
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.cantidad").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando la cantidad es negativa")
        void testCrear_DebeRetornar400ConCantidadNegativa() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Arroz Integral")
                    .categoria("Granos")
                    .precio(120.0)
                    .cantidad(-5) // Inválido
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.cantidad").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el nombre excede 100 caracteres")
        void testCrear_DebeRetornar400ConNombreLargo() throws Exception {
            // Given
            String nombreLargo = "A".repeat(101);
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre(nombreLargo)
                    .categoria("Granos")
                    .precio(120.0)
                    .cantidad(100)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe aceptar precio y cantidad en cero")
        void testCrear_DebeAceptarPrecioYCantidadEnCero() throws Exception {
            // Given
            ProductoDTO productoNuevo = ProductoDTO.builder()
                    .nombre("Producto Gratis")
                    .categoria("Promoción")
                    .precio(0.0) // Válido con @PositiveOrZero
                    .cantidad(0) // Válido con @PositiveOrZero
                    .build();

            ProductoDTO productoCreado = ProductoDTO.builder()
                    .id(5L)
                    .nombre("Producto Gratis")
                    .categoria("Promoción")
                    .precio(0.0)
                    .cantidad(0)
                    .build();

            when(productoService.create(any(ProductoDTO.class))).thenReturn(productoCreado);

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoNuevo)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.precio").value(0.0))
                    .andExpect(jsonPath("$.cantidad").value(0));

            verify(productoService, times(1)).create(any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 con múltiples errores de validación")
        void testCrear_DebeRetornar400ConMultiplesErrores() throws Exception {
            // Given
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("") // Inválido
                    .categoria("") // Inválido
                    .precio(null) // Inválido
                    .cantidad(null) // Inválido
                    .build();

            // When & Then
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                    .andExpect(jsonPath("$.fieldErrors.categoria").exists())
                    .andExpect(jsonPath("$.fieldErrors.precio").exists())
                    .andExpect(jsonPath("$.fieldErrors.cantidad").exists());

            verify(productoService, never()).create(any(ProductoDTO.class));
        }
    }

    // ==================== UPDATE ====================

    @Nested
    @DisplayName("PUT /api/productos/{idProducto} - Actualizar Producto")
    class ActualizarProductoTests {

        @Test
        @DisplayName("Debe actualizar producto exitosamente y retornar 200")
        void testActualizar_DebeActualizarProductoExitosamente() throws Exception {
            // Given
            Long idProducto = 1L;

            ProductoDTO productoActualizado = ProductoDTO.builder()
                    .nombre("Leche Descremada Actualizada")
                    .categoria("Lácteos")
                    .precio(90.0)
                    .cantidad(60)
                    .build();

            ProductoDTO productoResponse = ProductoDTO.builder()
                    .id(idProducto)
                    .nombre("Leche Descremada Actualizada")
                    .categoria("Lácteos")
                    .precio(90.0)
                    .cantidad(60)
                    .build();

            when(productoService.update(eq(idProducto), any(ProductoDTO.class)))
                    .thenReturn(productoResponse);

            // When & Then
            mockMvc.perform(put("/api/productos/{idProducto}", idProducto)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoActualizado)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(idProducto))
                    .andExpect(jsonPath("$.nombre").value("Leche Descremada Actualizada"))
                    .andExpect(jsonPath("$.categoria").value("Lácteos"))
                    .andExpect(jsonPath("$.precio").value(90.0))
                    .andExpect(jsonPath("$.cantidad").value(60));

            verify(productoService, times(1)).update(eq(idProducto), any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 con datos inválidos")
        void testActualizar_DebeRetornar400ConDatosInvalidos() throws Exception {
            // Given
            Long idProducto = 1L;
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("")
                    .categoria("")
                    .precio(null)
                    .cantidad(null)
                    .build();

            // When & Then
            mockMvc.perform(put("/api/productos/{idProducto}", idProducto)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors").exists());

            verify(productoService, never()).update(anyLong(), any(ProductoDTO.class));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el precio es negativo en actualización")
        void testActualizar_DebeRetornar400ConPrecioNegativo() throws Exception {
            // Given
            Long idProducto = 1L;
            ProductoDTO productoInvalido = ProductoDTO.builder()
                    .nombre("Leche")
                    .categoria("Lácteos")
                    .precio(-50.0)
                    .cantidad(10)
                    .build();

            // When & Then
            mockMvc.perform(put("/api/productos/{idProducto}", idProducto)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.precio").exists());

            verify(productoService, never()).update(anyLong(), any(ProductoDTO.class));
        }
    }

    // ==================== READ ====================

    @Nested
    @DisplayName("GET /api/productos - Listar Productos")
    class ListarProductosTests {

        @Test
        @DisplayName("Debe listar todos los productos exitosamente")
        void testListar_DebeListarTodosLosProductos() throws Exception {
            // Given
            List<ProductoDTO> productos = Arrays.asList(
                    ProductoDTO.builder()
                            .id(1L)
                            .nombre("Leche Descremada")
                            .categoria("Lácteos")
                            .precio(80.0)
                            .cantidad(50)
                            .build(),
                    ProductoDTO.builder()
                            .id(2L)
                            .nombre("Pan Integral")
                            .categoria("Panadería")
                            .precio(40.0)
                            .cantidad(100)
                            .build(),
                    ProductoDTO.builder()
                            .id(3L)
                            .nombre("Arroz Integral")
                            .categoria("Granos")
                            .precio(120.0)
                            .cantidad(75)
                            .build()
            );

            when(productoService.getAll()).thenReturn(productos);

            // When & Then
            mockMvc.perform(get("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Leche Descremada"))
                    .andExpect(jsonPath("$[0].categoria").value("Lácteos"))
                    .andExpect(jsonPath("$[0].precio").value(80.0))
                    .andExpect(jsonPath("$[0].cantidad").value(50))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].nombre").value("Pan Integral"))
                    .andExpect(jsonPath("$[2].id").value(3))
                    .andExpect(jsonPath("$[2].nombre").value("Arroz Integral"));

            verify(productoService, times(1)).getAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay productos")
        void testListar_DebeRetornarListaVacia() throws Exception {
            // Given
            when(productoService.getAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(productoService, times(1)).getAll();
        }
    }

    // ==================== DELETE ====================

    @Nested
    @DisplayName("DELETE /api/productos/{idProducto} - Eliminar Producto")
    class EliminarProductoTests {

        @Test
        @DisplayName("Debe eliminar producto exitosamente y retornar 204")
        void testEliminar_DebeEliminarProductoExitosamente() throws Exception {
            // Given
            Long idProducto = 1L;
            doNothing().when(productoService).delete(idProducto);

            // When & Then
            mockMvc.perform(delete("/api/productos/{idProducto}", idProducto))
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(productoService, times(1)).delete(idProducto);
        }

        @Test
        @DisplayName("Debe invocar el servicio de eliminación con el ID correcto")
        void testEliminar_DebeInvocarServicioConIdCorrecto() throws Exception {
            // Given
            Long idProducto = 99L;
            doNothing().when(productoService).delete(idProducto);

            // When & Then
            mockMvc.perform(delete("/api/productos/{idProducto}", idProducto))
                    .andExpect(status().isNoContent());

            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(productoService, times(1)).delete(captor.capture());
            assertThat(captor.getValue()).isEqualTo(99L);
        }
    }
}