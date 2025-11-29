package com.jcanare2.PruebaTecSupermercado.service;

import com.jcanare2.PruebaTecSupermercado.dto.SucursalDTO;
import com.jcanare2.PruebaTecSupermercado.model.Sucursal;

import java.util.List;

public interface ISucursalService {

    List<SucursalDTO> getAll();

    Sucursal getById(Long idSucursal);

    SucursalDTO create(SucursalDTO sucursalDTO);

    SucursalDTO update(Long idSucursal, SucursalDTO sucursalDTO);

    void delete(Long idSucursal);

}
