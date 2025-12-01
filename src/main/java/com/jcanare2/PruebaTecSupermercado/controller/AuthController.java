package com.jcanare2.PruebaTecSupermercado.controller;

import com.jcanare2.PruebaTecSupermercado.dto.AuthResponseDTO;
import com.jcanare2.PruebaTecSupermercado.dto.LoginRequestDTO;
import com.jcanare2.PruebaTecSupermercado.dto.RegisterRequestDTO;
import com.jcanare2.PruebaTecSupermercado.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }
}