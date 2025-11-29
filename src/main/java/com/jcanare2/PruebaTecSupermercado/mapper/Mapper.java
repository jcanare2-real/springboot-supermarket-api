package com.jcanare2.PruebaTecSupermercado.mapper;

import com.jcanare2.PruebaTecSupermercado.dto.DetalleVentaDTO;
import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.dto.SucursalDTO;
import com.jcanare2.PruebaTecSupermercado.dto.VentaDTO;
import com.jcanare2.PruebaTecSupermercado.model.Producto;
import com.jcanare2.PruebaTecSupermercado.model.Sucursal;
import com.jcanare2.PruebaTecSupermercado.model.Venta;

import java.util.stream.Collectors;

public class Mapper {


    //Mapeo de Producto a ProductoDTO
    public static ProductoDTO toDTO(Producto p) {
        if (p == null) return null;

        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .categoria(p.getCategoria())
                .precio(p.getPrecio())
                .build();
    }

    //Mapeo de Venta a VentaDTO
    public static VentaDTO toDTO(Venta v){
        if (v == null) return null;

        //Mapear Lista de detalle
        var detalle = v.getDetalle().stream().map(det ->
                DetalleVentaDTO.builder()
                        .id(det.getId())
                        .nombreProd(det.getProd().getNombre())
                        .cantProd(det.getCantidad())
                        .precio(det.getPrecio())
                        .subTotal(det.getPrecio() * det.getCantidad())
                        .build()
                ).collect(Collectors.toList());

        //Calcular Total
        var total = detalle.stream()
                .map(DetalleVentaDTO::getSubTotal)
                .reduce(0.00, Double::sum);

        return VentaDTO.builder()
                .id(v.getId())
                .fecha(v.getFecha())
                .idSucursal(v.getSucursal().getId())
                .estado(v.getEstado())
                .detalle(detalle)
                .total(total)
                .build();
    }

    //Mapeo de Sucursal a SucursalDTO
    public static SucursalDTO toDTO(Sucursal s){
        if (s == null) return  null;

        return SucursalDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .build();
    }

}
