package com.todocodeacademy.PruebaTecSupermercado.service;

import com.todocodeacademy.PruebaTecSupermercado.dto.SucursalDTO;
import com.todocodeacademy.PruebaTecSupermercado.model.Sucursal;

import java.util.List;

public interface ISucursalService {

    List<SucursalDTO> getAll();

    Sucursal getById(Long idSucursal);

    SucursalDTO create(SucursalDTO sucursalDTO);

    SucursalDTO update(Long idSucursal, SucursalDTO sucursalDTO);

    void delete(Long idSucursal);

}
