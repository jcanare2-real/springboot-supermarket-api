package com.jcanare2.PruebaTecSupermercado.service.impl;

import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.model.Producto;
import com.jcanare2.PruebaTecSupermercado.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Inicializa los Mocks automáticamente
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    // --- Tests de Inicialización ---

    @Test
    @DisplayName("Constructor: Debe lanzar NullPointerException si el repositorio es null")
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThatThrownBy(() -> new ProductoServiceImpl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("repo no debe ser null");
    }

    // --- Tests para getAll() ---

    @Test
    @DisplayName("getAll: Debe retornar una lista de DTOs cuando existen productos")
    void shouldReturnListOfProductDTOs() {
        // Given
        Producto prod1 = Producto.builder().id(1L).nombre("Arroz").categoria("Alimentos").precio(200.0).cantidad(10).build();
        Producto prod2 = Producto.builder().id(2L).nombre("Jabón").categoria("Limpieza").precio(150.0).cantidad(5).build();

        given(productoRepository.findAll()).willReturn(List.of(prod1, prod2));

        // When
        List<ProductoDTO> result = productoService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Arroz");
        assertThat(result.get(1).getCategoria()).isEqualTo("Limpieza");
    }

    @Test
    @DisplayName("getAll: Debe retornar lista vacía si no hay productos")
    void shouldReturnEmptyListWhenNoProducts() {
        // Given
        given(productoRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<ProductoDTO> result = productoService.getAll();

        // Then
        assertThat(result).isEmpty();
    }

    // --- Tests para create() ---

    @Test
    @DisplayName("create: Debe guardar el producto y retornar el DTO con ID")
    void shouldSaveAndReturnProductDTO() {
        // Given
        ProductoDTO inputDto = ProductoDTO.builder()
                .nombre("Leche")
                .categoria("Lácteos")
                .precio(100.0)
                .cantidad(20)
                .build();

        Producto savedEntity = Producto.builder()
                .id(1L) // Simulamos que la DB generó el ID
                .nombre("Leche")
                .categoria("Lácteos")
                .precio(100.0)
                .cantidad(20)
                .build();

        // Simulamos que al guardar cualquier producto, retorna la entidad con ID
        given(productoRepository.save(any(Producto.class))).willReturn(savedEntity);

        // When
        ProductoDTO result = productoService.create(inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Leche");
        verify(productoRepository).save(any(Producto.class));
    }

    // --- Tests para update() ---

    @Test
    @DisplayName("update: Debe actualizar campos y retornar DTO si el ID existe")
    void shouldUpdateExistingProduct() {
        // Given
        Long id = 1L;
        ProductoDTO updateDto = ProductoDTO.builder()
                .nombre("Leche Descremada") // Cambio de nombre
                .categoria("Lácteos")
                .precio(120.0)
                .cantidad(15)
                .build();

        Producto existingEntity = Producto.builder()
                .id(id)
                .nombre("Leche")
                .categoria("Lácteos")
                .precio(100.0)
                .cantidad(20)
                .build();

        // Simulamos que el repositorio devuelve una entidad actualizada tras el save
        Producto updatedEntity = Producto.builder()
                .id(id)
                .nombre("Leche Descremada")
                .categoria("Lácteos")
                .precio(120.0)
                .cantidad(15)
                .build();

        given(productoRepository.findById(id)).willReturn(Optional.of(existingEntity));
        given(productoRepository.save(any(Producto.class))).willReturn(updatedEntity);

        // When
        ProductoDTO result = productoService.update(id, updateDto);

        // Then
        assertThat(result.getNombre()).isEqualTo("Leche Descremada");
        assertThat(result.getPrecio()).isEqualTo(120.0);

        // Verificamos que se llamó al save
        verify(productoRepository).save(existingEntity);
    }

    @Test
    @DisplayName("update: Debe lanzar NotFoundException si el ID no existe")
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        // Given
        Long id = 99L;
        ProductoDTO updateDto = ProductoDTO.builder().nombre("Test").build();

        given(productoRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.update(id, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Producto no encontrado");

        verify(productoRepository, never()).save(any());
    }

    // --- Tests para delete() ---

    @Test
    @DisplayName("delete: Debe eliminar el producto si el ID existe")
    void shouldDeleteExistingProduct() {
        // Given
        Long id = 1L;
        Producto existingEntity = Producto.builder().id(id).nombre("Pan").build();

        given(productoRepository.findById(id)).willReturn(Optional.of(existingEntity));

        // When
        productoService.delete(id);

        // Then
        verify(productoRepository).delete(existingEntity);
    }

    @Test
    @DisplayName("delete: Debe lanzar NotFoundException si el ID no existe")
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {
        // Given
        Long id = 99L;
        given(productoRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Producto no encontrado");

        verify(productoRepository, never()).delete(any());
    }
}