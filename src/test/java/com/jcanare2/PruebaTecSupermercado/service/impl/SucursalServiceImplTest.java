package com.jcanare2.PruebaTecSupermercado.service.impl;

import com.jcanare2.PruebaTecSupermercado.dto.SucursalDTO;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.model.Sucursal;
import com.jcanare2.PruebaTecSupermercado.repository.SucursalRepository;
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

@ExtendWith(MockitoExtension.class)
class SucursalServiceImplTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalServiceImpl sucursalService;

    // --- Constructor Validation ---

    @Test
    @DisplayName("Constructor: Should throw NullPointerException when repo is null")
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThatThrownBy(() -> new SucursalServiceImpl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("repo no debe ser null");
    }

    // --- Tests for getAll() ---

    @Test
    @DisplayName("getAll: Should return list of SucursalDTOs")
    void shouldReturnListOfSucursalDTOs() {
        // Given
        Sucursal s1 = Sucursal.builder().id(1L).nombre("Centro").direccion("Av. 1").build();
        Sucursal s2 = Sucursal.builder().id(2L).nombre("Norte").direccion("Av. 2").build();

        given(sucursalRepository.findAll()).willReturn(List.of(s1, s2));

        // When
        List<SucursalDTO> result = sucursalService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Centro");
    }

    @Test
    @DisplayName("getAll: Should return empty list when no data")
    void shouldReturnEmptyListWhenNoData() {
        // Given
        given(sucursalRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<SucursalDTO> result = sucursalService.getAll();

        // Then
        assertThat(result).isEmpty();
    }

    // --- Tests for create() ---

    @Test
    @DisplayName("create: Should save and return DTO with ID")
    void shouldSaveAndReturnSucursalDTO() {
        // Given
        SucursalDTO inputDto = SucursalDTO.builder()
                .nombre("Sur")
                .direccion("Calle 123")
                .build();

        Sucursal savedEntity = Sucursal.builder()
                .id(10L) // ID generado por DB
                .nombre("Sur")
                .direccion("Calle 123")
                .build();

        given(sucursalRepository.save(any(Sucursal.class))).willReturn(savedEntity);

        // When
        SucursalDTO result = sucursalService.create(inputDto);

        // Then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("Sur");
        verify(sucursalRepository).save(any(Sucursal.class));
    }

    // --- Tests for update() ---

    @Test
    @DisplayName("update: Should update fields and return DTO when ID exists")
    void shouldUpdateExistingSucursal() {
        // Given
        Long id = 1L;
        SucursalDTO updateDto = SucursalDTO.builder()
                .nombre("Sucursal Modificada")
                .direccion("Nueva Dirección 555")
                .build();

        Sucursal existingEntity = Sucursal.builder()
                .id(id)
                .nombre("Sucursal Original")
                .direccion("Dirección Vieja")
                .build();

        // Entidad esperada tras la modificación
        Sucursal updatedEntity = Sucursal.builder()
                .id(id)
                .nombre("Sucursal Modificada")
                .direccion("Nueva Dirección 555")
                .build();

        given(sucursalRepository.findById(id)).willReturn(Optional.of(existingEntity));
        given(sucursalRepository.save(any(Sucursal.class))).willReturn(updatedEntity);

        // When
        SucursalDTO result = sucursalService.update(id, updateDto);

        // Then
        assertThat(result.getNombre()).isEqualTo("Sucursal Modificada");
        assertThat(result.getDireccion()).isEqualTo("Nueva Dirección 555");

        // Verificación extra: Asegurar que el repositorio buscó por ID y guardó
        verify(sucursalRepository).findById(id);
        verify(sucursalRepository).save(existingEntity);
    }

    @Test
    @DisplayName("update: Should throw NotFoundException when ID does not exist")
    void shouldThrowExceptionWhenUpdatingNonExistingId() {
        // Given
        Long id = 99L;
        SucursalDTO updateDto = SucursalDTO.builder().nombre("Test").build();

        given(sucursalRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sucursalService.update(id, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sucursal no encontrada"); // Ojo: Tu mensaje en el service dice "Sucursal no encontrada"

        verify(sucursalRepository, never()).save(any());
    }

    // --- Tests for delete() ---

    @Test
    @DisplayName("delete: Should delete entity when ID exists")
    void shouldDeleteExistingSucursal() {
        // Given
        Long id = 1L;
        Sucursal entity = Sucursal.builder().id(id).nombre("Test").build();

        given(sucursalRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        sucursalService.delete(id);

        // Then
        verify(sucursalRepository).delete(entity);
    }

    @Test
    @DisplayName("delete: Should throw NotFoundException when ID does not exist")
    void shouldThrowExceptionWhenDeletingNonExistingId() {
        // Given
        Long id = 99L;
        given(sucursalRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sucursalService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sucursal no encontrada");
    }

    // --- Tests for getById() (Public method logic) ---

    @Test
    @DisplayName("getById: Should return Sucursal entity when found")
    void shouldReturnEntityWhenFound() {
        // Given
        Long id = 1L;
        Sucursal entity = Sucursal.builder().id(id).nombre("Test").build();
        given(sucursalRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        Sucursal result = sucursalService.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getById: Should throw NotFoundException when not found")
    void shouldThrowExceptionWhenGetByIdNotFound() {
        // Given
        Long id = 50L;
        given(sucursalRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sucursalService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sucursal no encontrada");
    }
}