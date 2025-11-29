package com.jcanare2.PruebaTecSupermercado.dto;

import com.jcanare2.PruebaTecSupermercado.enums.EstadoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDTO {

    private Long id;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private EstadoEnum estado;

    @NotNull
    private Long idSucursal;

    @NotNull
    @Size(min = 1, message = "La venta debe tener al menos un ítem de detalle")
    private List<@Valid DetalleVentaDTO> detalle;

    @NotNull
    @PositiveOrZero
    private Double total;
}
