package com.todocodeacademy.PruebaTecSupermercado.model;

import com.todocodeacademy.PruebaTecSupermercado.enums.EstadoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private EstadoEnum estado;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalle = new ArrayList<>();

    private Double total;
}
