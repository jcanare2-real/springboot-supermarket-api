package com.jcanare2.PruebaTecSupermercado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class LoginRequestDTO {

    private String username;

    private String password;
}
