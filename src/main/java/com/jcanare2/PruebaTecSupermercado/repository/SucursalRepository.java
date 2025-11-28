package com.todocodeacademy.PruebaTecSupermercado.repository;

import com.todocodeacademy.PruebaTecSupermercado.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
}
