package com.jcanare2.PruebaTecSupermercado.service.impl;

import com.jcanare2.PruebaTecSupermercado.dto.ProductoDTO;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.mapper.Mapper;
import com.jcanare2.PruebaTecSupermercado.model.Producto;
import com.jcanare2.PruebaTecSupermercado.repository.ProductoRepository;
import com.jcanare2.PruebaTecSupermercado.service.IProductoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository repo;

    public ProductoServiceImpl(ProductoRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo no debe ser null");
    }

    @Override
    public List<ProductoDTO> getAll() {
        return repo.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public ProductoDTO create(ProductoDTO productoDTO) {

        Producto prod = Producto.builder()
                .nombre(productoDTO.getNombre())
                .categoria(productoDTO.getCategoria())
                .precio(productoDTO.getPrecio())
                .cantidad(productoDTO.getCantidad())
                .build();
        return Mapper.toDTO(repo.save(prod));
    }

    @Override
    public ProductoDTO update(Long idProducto, ProductoDTO productoDTO) {

        //Buscar producto a ver si existe
        Producto prod = this.getById(idProducto);
         prod.setNombre(productoDTO.getNombre());
         prod.setCategoria(productoDTO.getCategoria());
         prod.setPrecio(productoDTO.getPrecio());
         prod.setCantidad(productoDTO.getCantidad());

        return Mapper.toDTO(repo.save(prod));
    }

    @Override
    public void delete(Long idProducto) {
        //Buscar producto a ver si existe
        Producto prod = this.getById(idProducto);
        repo.delete(prod);
    }

    private Producto getById(Long idProducto){
        return repo.findById(idProducto)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
}