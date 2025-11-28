package com.todocodeacademy.PruebaTecSupermercado.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Este record define cómo se verá el JSON de error
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> fieldErrors
) {}
