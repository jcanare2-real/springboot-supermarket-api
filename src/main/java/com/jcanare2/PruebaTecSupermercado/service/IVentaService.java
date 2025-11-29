package com.jcanare2.PruebaTecSupermercado.service;

import com.jcanare2.PruebaTecSupermercado.dto.VentaDTO;

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
