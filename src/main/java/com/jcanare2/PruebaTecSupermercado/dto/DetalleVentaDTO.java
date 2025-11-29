package com.jcanare2.PruebaTecSupermercado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaDTO {

    private Long id;

    @NotBlank
    private String nombreProd;

    @NotNull
    @Positive
    private Integer cantProd;

    @NotNull
    @PositiveOrZero
    private Double precio;

    @NotNull
    @PositiveOrZero
    private Double subTotal;
}
