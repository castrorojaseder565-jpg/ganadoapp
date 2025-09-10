package com.mi.proyecto.ganado.ganadoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 👉 Registramos BCrypt como bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 👉 Usuarios en memoria para pruebas
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(
                User.withUsername("admin@correo.com")
                        .password(passwordEncoder().encode("admin123"))
                        .roles("ADMIN")
                        .build()
        );

        manager.createUser(
                User.withUsername("veterinario@correo.com")
                        .password(passwordEncoder().encode("vet123"))
                        .roles("VETERINARIO")
                        .build()
        );

        return manager;
    }

    // 👉 Configuración de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/veterinario/**").hasAnyRole("ADMIN", "VETERINARIO")
                        .requestMatchers("/ganado/**").hasAnyRole("ADMIN", "VETERINARIO")
                        .requestMatchers("/vacunas/**").hasAnyRole("ADMIN", "VETERINARIO")
                        .requestMatchers("/", "/login", "/public/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                 // 👉 Tu vista de login
                        .defaultSuccessUrl("/ganado/lista", true) // 👉 Después de iniciar sesión
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
