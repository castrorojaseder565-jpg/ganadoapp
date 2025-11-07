package com.mi.proyecto.ganado.ganadoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Bean para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para el AuthenticationManager (login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configuración de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Acceso para veterinarios y administradores
                        .requestMatchers("/ganado/lista", "/vacuna/**").hasAnyRole("VETERINARIO", "ADMIN")

                        // Solo administrador puede editar o eliminar ganado
                        .requestMatchers("/ganado/editar/**", "/ganado/eliminar/**").hasRole("ADMIN")

                        // Solo administrador para estadísticas y administración
                        .requestMatchers("/estadisticas/**", "/admin/**").hasRole("ADMIN")

                        // Páginas públicas
                        .requestMatchers("/", "/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()

                        .anyRequest().authenticated()
                )
                // Configuración del login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/ganado/lista", true)
                        .permitAll()
                )
                // Configuración del logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }
}
