package com.todocodeacademy.PruebaTecSupermercado.repository;

import com.todocodeacademy.PruebaTecSupermercado.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {



}
