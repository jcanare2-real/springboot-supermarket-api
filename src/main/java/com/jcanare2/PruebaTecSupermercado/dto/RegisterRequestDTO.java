package com.jcanare2.PruebaTecSupermercado.dto;

import com.jcanare2.PruebaTecSupermercado.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class RegisterRequestDTO {

    private String nombre;

    private String apellido;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
