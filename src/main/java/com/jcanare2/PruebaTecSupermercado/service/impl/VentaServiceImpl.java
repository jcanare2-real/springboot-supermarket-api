package com.jcanare2.PruebaTecSupermercado.service.impl;

import com.jcanare2.PruebaTecSupermercado.dto.DetalleVentaDTO;
import com.jcanare2.PruebaTecSupermercado.dto.VentaDTO;
import com.jcanare2.PruebaTecSupermercado.exception.NotFoundException;
import com.jcanare2.PruebaTecSupermercado.mapper.Mapper;
import com.jcanare2.PruebaTecSupermercado.model.DetalleVenta;
import com.jcanare2.PruebaTecSupermercado.model.Producto;
import com.jcanare2.PruebaTecSupermercado.model.Sucursal;
import com.jcanare2.PruebaTecSupermercado.model.Venta;
import com.jcanare2.PruebaTecSupermercado.repository.ProductoRepository;
import com.jcanare2.PruebaTecSupermercado.repository.SucursalRepository;
import com.jcanare2.PruebaTecSupermercado.repository.VentaRepository;
import com.jcanare2.PruebaTecSupermercado.service.ISucursalService;
import com.jcanare2.PruebaTecSupermercado.service.IVentaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository repo;

    private final ProductoRepository productoRepository;

    private final ISucursalService sucursalService;

    public VentaServiceImpl(VentaRepository repo,
                            ProductoRepository productoRepository,
                            ISucursalService sucursalService) {
        this.repo = repo;
        this.productoRepository = productoRepository;
        this.sucursalService = sucursalService;
    }

    @Override
    public List<VentaDTO> getAll() {

        List<Venta> ventas = this.repo.findAll();
        List<VentaDTO> ventasDTO = new ArrayList<>();

        VentaDTO dto;
        for (Venta v : ventas){
            dto = Mapper.toDTO(v);
            ventasDTO.add(dto);
        }
        return ventasDTO;
    }

    @Override
    public List<VentaDTO> getVentasBySucursal(Long idSucursal) {
        //Buscar Sucursal
        Sucursal suc = sucursalService.getById(idSucursal);
        if (suc == null){
            throw  new NotFoundException("Sucursal no encontrada");
        }
        return repo.findBySucursal(suc).stream().map(Mapper::toDTO).toList();
    }

    @Override
    public List<VentaDTO> getByDateRange(LocalDate from, LocalDate until) {
        return List.of();
    }

    @Override
    public VentaDTO create(VentaDTO ventaDTO) {

        //Validaciones
        if(ventaDTO == null) throw new RuntimeException("VentaDTO es null");
        if(ventaDTO.getIdSucursal() == null) throw new RuntimeException("Debe indicar la sucursal");
        if(ventaDTO.getDetalle() == null || ventaDTO.getDetalle().isEmpty())
            throw new RuntimeException("Debe incluir al menos un producto");

        //Buscar Sucursal
        Sucursal suc = sucursalService.getById(ventaDTO.getIdSucursal());
        if (suc == null){
            throw  new NotFoundException("Sucursal no encontrada");
        }

        //Crear Venta
        Venta venta = new Venta();
        venta.setFecha(ventaDTO.getFecha());
        venta.setEstado(ventaDTO.getEstado());
        venta.setSucursal(suc);
        venta.setTotal(ventaDTO.getTotal());

        //Lista de detalle
        List<DetalleVenta> detalles = new ArrayList<>();

        for (DetalleVentaDTO dvDTO : ventaDTO.getDetalle()){
            Producto p = productoRepository.findByNombre(dvDTO.getNombreProd()).orElse(null);
            if (p == null)
                throw new NotFoundException("Producto no encontrado " + dvDTO.getNombreProd());

            //Crear DetalleVenta
            DetalleVenta detVenta = DetalleVenta.builder()
                    .venta(venta)
                    .prod(p)
                    .cantidad(dvDTO.getCantProd())
                    .precio(dvDTO.getPrecio())
                    .build();
            detalles.add(detVenta);

            //Se podría ir calculando el SubTotal
            // para calcular el Total en caso que no venga en el ventaDTO
        }

        //Setear Lista DetalleVenta en la Venta
        venta.setDetalle(detalles);

        //Guardar en la BD y retornar DTO usando el Mapper
        return Mapper.toDTO(repo.save(venta));
    }

    @Override
    public VentaDTO update(Long idVenta, VentaDTO ventaDTO) {

        //Buscar si la venta existe
        Venta venta = this.getById(idVenta);

        //Setear campos
        if (ventaDTO.getFecha() != null){
            venta.setFecha(ventaDTO.getFecha());
        }
        if (ventaDTO.getIdSucursal() != null){
            Sucursal suc = sucursalService.getById(ventaDTO.getIdSucursal());
            venta.setSucursal(suc);
        }
        if (ventaDTO.getEstado() != null){
            venta.setEstado(ventaDTO.getEstado());
        }
        if (ventaDTO.getTotal() != null){
            venta.setTotal(ventaDTO.getTotal());
        }

        return Mapper.toDTO(repo.save(venta));
    }

    @Override
    public void delete(Long idVenta) {
        //Buscar la venta
        Venta venta = this.getById(idVenta);

        //Eliminar la Venta
        repo.delete(venta);
    }

    private Venta getById(Long idVenta){
        return repo.findById(idVenta)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada " + idVenta));
    }
}
