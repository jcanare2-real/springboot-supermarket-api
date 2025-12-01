package com.jcanare2.PruebaTecSupermercado.service;

import com.jcanare2.PruebaTecSupermercado.config.JwtService;
import com.jcanare2.PruebaTecSupermercado.dto.AuthResponseDTO;
import com.jcanare2.PruebaTecSupermercado.dto.LoginRequestDTO;
import com.jcanare2.PruebaTecSupermercado.dto.RegisterRequestDTO;
import com.jcanare2.PruebaTecSupermercado.enums.Role;
import com.jcanare2.PruebaTecSupermercado.model.Usuario;
import com.jcanare2.PruebaTecSupermercado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        var user = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Por defecto USER, puedes cambiarlo
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder().token(jwtToken).build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder().token(jwtToken).build();
    }
}