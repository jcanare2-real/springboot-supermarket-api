package com.todocodeacademy.PruebaTecSupermercado.service;

import com.todocodeacademy.PruebaTecSupermercado.dto.VentaDTO;
import com.todocodeacademy.PruebaTecSupermercado.model.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {

    List<VentaDTO> getAll();

    List<VentaDTO> getVentasBySucursal(Long idSucursal);

    List<VentaDTO> getByDateRange(LocalDate from, LocalDate until);

    VentaDTO create(VentaDTO ventaDTO);

    VentaDTO update(Long idVenta, VentaDTO ventaDTO);

    void delete(Long idVenta);
}
