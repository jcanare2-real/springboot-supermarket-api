package com.jcanare2.PruebaTecSupermercado.service.impl;

import com.jcanare2.PruebaTecSupermercado.dto.DetalleVentaDTO;
import com.jcanare2.PruebaTecSupermercado.enums.EstadoEnum;
import com.jcanare2.PruebaTecSupermercado.dto.VentaDTO;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.model.DetalleVenta;
import com.jcanare2.PruebaTecSupermercado.model.Producto;
import com.jcanare2.PruebaTecSupermercado.model.Sucursal;
import com.jcanare2.PruebaTecSupermercado.model.Venta;
import com.jcanare2.PruebaTecSupermercado.repository.ProductoRepository;
import com.jcanare2.PruebaTecSupermercado.repository.SucursalRepository;
import com.jcanare2.PruebaTecSupermercado.repository.VentaRepository;
import com.jcanare2.PruebaTecSupermercado.service.ISucursalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ISucursalService sucursalService;

    @InjectMocks
    private VentaServiceImpl ventaService;


    // --- Tests de getVentasBySucursal ---

    @Test
    @DisplayName("getVentasBySucursal: Debe buscar sucursal y filtrar ventas por ella")
    void shouldFindSucursalAndReturnFilteredVentas() {
        // Given
        Long idSucursal = 1L;
        Sucursal sucursalMock = Sucursal.builder().id(idSucursal).nombre("Norte").build();

        Venta venta1 = Venta.builder()
                .id(100L)
                .sucursal(sucursalMock)
                .detalle(new ArrayList<>())
                .total(100.0)
                .build();

        given(sucursalService.getById(idSucursal)).willReturn(sucursalMock);
        given(ventaRepository.findBySucursal(sucursalMock)).willReturn(List.of(venta1));

        // When
        List<VentaDTO> result = ventaService.getVentasBySucursal(idSucursal);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);

        verify(sucursalService).getById(idSucursal);
        verify(ventaRepository).findBySucursal(sucursalMock);
    }

    @Test
    @DisplayName("getVentasBySucursal: Debe lanzar excepción si el servicio retorna null")
    void shouldThrowExceptionWhenSucursalServiceReturnsNull() {
        Long idSucursal = 99L;
        given(sucursalService.getById(idSucursal)).willReturn(null);

        assertThatThrownBy(() -> ventaService.getVentasBySucursal(idSucursal))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sucursal no encontrada");

        verify(ventaRepository, never()).findBySucursal(any());
    }


    // --- Tests de create ---

    @Test
    @DisplayName("create: Debe crear venta usando SucursalService")
    void shouldCreateVentaSuccessfully() {
        // Given
        VentaDTO inputDto = VentaDTO.builder()
                .fecha(LocalDate.now())
                .estado(EstadoEnum.REGISTRADA)
                .idSucursal(1L)
                .total(300.0)
                .detalle(List.of(DetalleVentaDTO.builder()
                        .nombreProd("Pan")
                        .cantProd(1)
                        .precio(10.0)
                        .build()))
                .build();

        Sucursal sucursalMock = Sucursal.builder().id(1L).nombre("Norte").build();
        Producto productoMock = Producto.builder().id(5L).nombre("Pan").precio(10.0).build();

        // Detalle interno para el guardado
        DetalleVenta detalleVenta = DetalleVenta.builder()
                .prod(productoMock)
                .cantidad(1)
                .precio(10.0)
                .build();

        // CORRECCIÓN: El objeto guardado debe tener sucursal y lista de detalles
        Venta savedVenta = Venta.builder()
                .id(500L)
                .sucursal(sucursalMock)
                .detalle(List.of(detalleVenta)) // <--- NECESARIO
                .total(300.0)
                .build();

        // Asignamos la venta al detalle (bidireccionalidad simulada)
        detalleVenta.setVenta(savedVenta);

        given(sucursalService.getById(1L)).willReturn(sucursalMock);
        given(productoRepository.findByNombre("Pan")).willReturn(Optional.of(productoMock));
        given(ventaRepository.save(any(Venta.class))).willReturn(savedVenta);

        // When
        VentaDTO result = ventaService.create(inputDto);

        // Then
        assertThat(result.getId()).isEqualTo(500L);
        verify(sucursalService).getById(1L);
        verify(ventaRepository).save(any(Venta.class));
    }


    // --- Tests de update ---

    @Test
    @DisplayName("update: Debe actualizar y cambiar sucursal si se solicita")
    void shouldUpdateVentaAndChangeSucursal() {
        // Given
        Long idVenta = 1L;
        Long idNuevaSucursal = 2L;
        Double totalEsperado = 999.0;

        VentaDTO updateDto = VentaDTO.builder()
                .idSucursal(idNuevaSucursal)
                .total(totalEsperado)
                .build();

        // Venta original en BD
        Venta ventaExistente = Venta.builder()
                .id(idVenta)
                .sucursal(Sucursal.builder().id(1L).build())
                .detalle(new ArrayList<>())
                .total(100.0)
                .build();

        Sucursal nuevaSucursal = Sucursal.builder().id(idNuevaSucursal).nombre("Sur").build();
        
        // Creamos un detalle ficticio que sume 999.0 para que el Mapper devuelva el total correcto
        Producto prodDummy = Producto.builder().id(9L).nombre("Dummy").precio(totalEsperado).build();
        DetalleVenta detalleDummy = DetalleVenta.builder()
                .prod(prodDummy)
                .cantidad(1)
                .precio(totalEsperado)
                // Si tu entidad DetalleVenta tiene subtotal, asegúrate de setearlo también
                // .subTotal(totalEsperado)
                .build();

        // Mock de lo que retorna el save
        Venta ventaGuardada = Venta.builder()
                .id(idVenta)
                .sucursal(nuevaSucursal)
                .detalle(List.of(detalleDummy)) // <--- AHORA TIENE DETALLES QUE SUMAN 999.0
                .total(totalEsperado)
                .build();

        given(ventaRepository.findById(idVenta)).willReturn(Optional.of(ventaExistente));
        given(sucursalService.getById(idNuevaSucursal)).willReturn(nuevaSucursal);
        given(ventaRepository.save(any(Venta.class))).willReturn(ventaGuardada);

        // When
        VentaDTO result = ventaService.update(idVenta, updateDto);

        // Then
        // Verificamos que el resultado del DTO sea correcto (gracias al detalle dummy)
        assertThat(result.getTotal()).isEqualTo(totalEsperado);

        // Verificamos la interacción con el servicio de sucursales
        verify(sucursalService).getById(idNuevaSucursal);

        // Opcional pero recomendado: Verificar que el setTotal se llamó en la entidad original
        // aunque el Mapper luego lo sobreescriba con la suma de detalles.
        assertThat(ventaExistente.getTotal()).isEqualTo(totalEsperado);
    }

    // --- Validaciones y Casos Borde ---

    @Test
    @DisplayName("Validaciones: Debe lanzar RuntimeException si el input es inválido")
    void shouldValidateInputFields() {
        assertThatThrownBy(() -> ventaService.create(null))
                .hasMessage("VentaDTO es null");

        VentaDTO noSucursal = VentaDTO.builder().idSucursal(null).build();
        assertThatThrownBy(() -> ventaService.create(noSucursal))
                .hasMessage("Debe indicar la sucursal");

        VentaDTO noDetalle = VentaDTO.builder().idSucursal(1L).detalle(null).build();
        assertThatThrownBy(() -> ventaService.create(noDetalle))
                .hasMessage("Debe incluir al menos un producto");
    }

    @Test
    @DisplayName("delete: Debe eliminar venta si existe")
    void shouldDeleteExistingVenta() {
        Long id = 1L;
        Venta venta = Venta.builder()
                .id(id)
                .detalle(new ArrayList<>()) // Por seguridad, aunque delete no usa mapper
                .build();

        given(ventaRepository.findById(id)).willReturn(Optional.of(venta));

        ventaService.delete(id);

        verify(ventaRepository).delete(venta);
    }

    @Test
    @DisplayName("delete: Debe lanzar excepción si no existe")
    void shouldThrowWhenDeletingNonExistingVenta() {
        given(ventaRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getAll: Retorna lista mapeada")
    void shouldReturnAll() {
        // Given
        Sucursal suc = Sucursal.builder().id(1L).nombre("Dummy").build();

        // CORRECCIÓN: Inicializar Sucursal y Detalle
        Venta venta = Venta.builder()
                .id(10L)
                .sucursal(suc)
                .detalle(new ArrayList<>()) // <--- NECESARIO
                .total(100.0)
                .build();

        given(ventaRepository.findAll()).willReturn(List.of(venta));

        // When
        List<VentaDTO> result = ventaService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdSucursal()).isEqualTo(1L);
    }
}