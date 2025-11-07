package com.mi.proyecto.ganado.ganadoapp.controller;


import com.mi.proyecto.ganado.ganadoapp.mode.Usuario;
import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String bienvenido() {
        return "bienvenido";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro-veterinario";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            // Asignar rol por defecto "VETERINARIO"
            usuario.setRol("VETERINARIO");

            usuarioService.registrarUsuario(usuario);
            model.addAttribute("exito", "Usuario registrado exitosamente 🎉");
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "registro-veterinario";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

