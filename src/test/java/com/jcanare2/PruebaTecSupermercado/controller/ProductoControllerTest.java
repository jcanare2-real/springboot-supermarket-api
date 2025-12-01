package com.jcanare2.PruebaTecSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcanare2.PruebaTecSupermercado.config.JwtAuthenticationFilter;
import com.jcanare2.PruebaTecSupermercado.config.JwtService;
import com.jcanare2.PruebaTecSupermercado.config.SecurityConfig;
import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.repository.UsuarioRepository;
import com.jcanare2.PruebaTecSupermercado.service.IProductoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)
@DisplayName("ProductoController - Tests Unitarios")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductoService productoService;

    // --- MOCKS PARA QUE ARRANQUE LA CONFIGURACIÓN ---
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    // NOTA: Ya NO mockeamos JwtAuthenticationFilter aquí con @MockBean,
    // lo definimos abajo en TestConfig para que no rompa la cadena.

    // ==========================================
    // CONFIGURACIÓN MANUAL DEL FILTRO PARA TESTS
    // ==========================================
    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
            // Creamos una subclase anónima que SOBREESCRIBE el método doFilterInternal.
            // En lugar de validar tokens, simplemente llama a filterChain.doFilter().
            // Esto permite que @WithMockUser funcione correctamente.
            return new JwtAuthenticationFilter(jwtService, userDetailsService) {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }

        @Bean
        public UserDetailsService userDetailsService() {
            // Bean dummy necesario para el constructor del filtro original
            return username -> null;
        }
    }

    // ==================== TESTS ====================

    @Test
    @DisplayName("POST: Crear Producto Exitosamente (ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCrear_DebeCrearProductoExitosamente() throws Exception {
        ProductoDTO productoNuevo = ProductoDTO.builder()
                .nombre("Arroz Integral")
                .categoria("Granos")
                .precio(120.00)
                .cantidad(100)
                .build();

        ProductoDTO productoCreado = ProductoDTO.builder().id(3L).nombre("Arroz Integral").build();

        when(productoService.create(any(ProductoDTO.class))).thenReturn(productoCreado);

        mockMvc.perform(post("/api/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoNuevo)))
                .andExpect(status().isCreated()) // isCreated() si tu controller retorna 201
                .andExpect(jsonPath("$.nombre").value("Arroz Integral"));
    }

    @Test
    @DisplayName("POST: Retorna 400 si nombre vacío (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testCrear_DebeRetornar400ConNombreVacio() throws Exception {
        ProductoDTO productoInvalido = ProductoDTO.builder().nombre("").build();

        mockMvc.perform(post("/api/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest()); // Ahora sí llegará al controller y validará
    }

    @Test
    @DisplayName("GET: Listar Productos (USER)")
    @WithMockUser(roles = "USER")
    void testListar_DebeListarTodosLosProductos() throws Exception {
        when(productoService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("SEGURIDAD: USER no puede borrar productos (403)")
    @WithMockUser(username = "pepe", roles = "USER")
    void testDelete_ForbiddenForUser() throws Exception {
        mockMvc.perform(delete("/api/productos/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(productoService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("SEGURIDAD: Usuario anónimo no puede ver productos (403)")
    void testGetAll_ForbiddenAnonymous() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isForbidden());
    }
}