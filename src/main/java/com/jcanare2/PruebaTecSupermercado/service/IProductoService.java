package com.jcanare2.PruebaTecSupermercado.service;

import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;

import java.util.List;

public interface IProductoService {

    List<ProductoDTO> getAll();

    ProductoDTO create(ProductoDTO productDTO);

    ProductoDTO update(Long idProducto, ProductoDTO productoDTO);

    void delete(Long idProducto);
}
