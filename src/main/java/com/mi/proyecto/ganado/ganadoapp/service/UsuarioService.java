package com.mi.proyecto.ganado.ganadoapp.service;


import com.mi.proyecto.ganado.ganadoapp.mode.Usuario;
import com.mi.proyecto.ganado.ganadoapp.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("❌ El correo ya está registrado.");
        }


        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("VETERINARIO");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }

    /**
     * Carga un usuario por su correo (para Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles(usuario.getRol()) // ejemplo: "ADMIN", "VETERINARIO"
                .build();
    }

    /**
     * Crea el usuario administrador automáticamente si no existe
     */
    @PostConstruct
    public void crearAdminPorDefecto() {
        String correoAdmin = "admin@admin.com";

        if (usuarioRepository.findByCorreo(correoAdmin).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador General");
            admin.setCorreo(correoAdmin);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");

            usuarioRepository.save(admin);
            System.out.println("✅ Admin creado correctamente (correo: admin@admin.com / pass: admin123)");
        }
    }
}
