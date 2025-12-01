package com.jcanare2.PruebaTecSupermercado.repository;

import com.jcanare2.PruebaTecSupermercado.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
