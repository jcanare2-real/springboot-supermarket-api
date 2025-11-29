package com.jcanare2.PruebaTecSupermercado.repository;

import com.jcanare2.PruebaTecSupermercado.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    //Buscar producto por nombre
    Optional<Producto> findByNombre(String nombre);
}
