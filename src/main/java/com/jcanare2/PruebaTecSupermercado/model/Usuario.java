package com.jcanare2.PruebaTecSupermercado.model;

import com.jcanare2.PruebaTecSupermercado.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String username; // Puede ser email o nombre de usuario

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // --- Lógica de Spring Security ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retornamos el rol como autoridad. Spring espera "ROLE_" a veces,
        // pero para validaciones simples el nombre del enum sirve.
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

    }

    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}