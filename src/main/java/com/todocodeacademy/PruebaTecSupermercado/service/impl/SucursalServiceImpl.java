package com.todocodeacademy.PruebaTecSupermercado.service.impl;

import com.todocodeacademy.PruebaTecSupermercado.dto.SucursalDTO;
import com.todocodeacademy.PruebaTecSupermercado.exception.NotFoundException;
import com.todocodeacademy.PruebaTecSupermercado.mapper.Mapper;
import com.todocodeacademy.PruebaTecSupermercado.model.Sucursal;
import com.todocodeacademy.PruebaTecSupermercado.repository.SucursalRepository;
import com.todocodeacademy.PruebaTecSupermercado.service.ISucursalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SucursalServiceImpl implements ISucursalService {

    private final SucursalRepository repo;

    public SucursalServiceImpl(SucursalRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo no debe ser null");
    }

    @Override
    public List<SucursalDTO> getAll() {
        return repo.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public SucursalDTO create(SucursalDTO sucursalDTO) {
        Sucursal suc = Sucursal.builder()
                .nombre(sucursalDTO.getNombre())
                .direccion(sucursalDTO.getDireccion())
                .build();
        return Mapper.toDTO(repo.save(suc));
    }

    @Override
    public SucursalDTO update(Long idSucursal, SucursalDTO sucursalDTO) {

        Sucursal suc = this.getById(idSucursal);
        suc.setNombre(sucursalDTO.getNombre());
        suc.setDireccion(suc.getDireccion());

        return Mapper.toDTO(repo.save(suc));
    }

    @Override
    public void delete(Long idSucursal) {
        Sucursal suc = this.getById(idSucursal);
        repo.delete(suc);
    }

    @Override
    public Sucursal getById(Long idSucursal){
        return repo.findById(idSucursal)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }
}
