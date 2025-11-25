package com.todocodeacademy.PruebaTecSupermercado.service;

import com.todocodeacademy.PruebaTecSupermercado.dto.ProductoDTO;
import com.todocodeacademy.PruebaTecSupermercado.model.Producto;

import java.util.List;

public interface IProductoService {

    List<ProductoDTO> getAll();

    ProductoDTO create(ProductoDTO productDTO);

    ProductoDTO update(Long idProducto, ProductoDTO productoDTO);

    void delete(Long idProducto);
}
