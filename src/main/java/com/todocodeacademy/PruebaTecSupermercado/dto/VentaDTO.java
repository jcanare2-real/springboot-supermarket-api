package com.todocodeacademy.PruebaTecSupermercado.dto;

import com.todocodeacademy.PruebaTecSupermercado.enums.EstadoEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDTO {

    //Datos de la venta
    private Long id;
    private LocalDate fecha;

    private EstadoEnum estado;

    //Datos de la sucursal
    private Long idSucursal;

    //Detalle de la venta
    private List<DetalleVentaDTO> detalle;

    //Total de la venta
    private Double total;
}
