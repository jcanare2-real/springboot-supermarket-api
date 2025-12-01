package com.jcanare2.PruebaTecSupermercado.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

    private String token;
}
