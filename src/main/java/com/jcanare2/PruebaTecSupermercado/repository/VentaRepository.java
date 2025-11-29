package com.jcanare2.PruebaTecSupermercado.repository;

import com.jcanare2.PruebaTecSupermercado.model.Sucursal;
import com.jcanare2.PruebaTecSupermercado.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findBySucursal(Sucursal sucursal);

}
