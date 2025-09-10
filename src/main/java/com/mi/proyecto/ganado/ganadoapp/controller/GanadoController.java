package com.mi.proyecto.ganado.ganadoapp.controller;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ganado")
public class GanadoController {


    @Autowired
    private GanadoService ganadoService;

    @Autowired
    private VacunaService vacunaService;


    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        System.out.println("Accediendo al formulario de registro...");
        model.addAttribute("ganado", new Ganado());
        return "registro-ganado";
    }


    @PostMapping("/guardar")
    public String guardarGanado(@ModelAttribute Ganado ganado) {
        ganadoService.guardarGanado(ganado);  // Guarda el nuevo ganado
        return "redirect:/ganado/detalle/" + ganado.getId();  // Redirige a su vista de detalle
    }


    @GetMapping("/lista")
    public String listarGanado(Model model) {
        model.addAttribute("ganados", ganadoService.listarGanados());
        return "lista-ganado";
    }

    @GetMapping("/detalle/{id}")
    public String detalleGanado(@PathVariable Long id, Model model) {
        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        model.addAttribute("ganado", ganado);
        model.addAttribute("vacuna", new Vacuna()); // para formulario vacuna
        return "detalle-ganado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGanado(@PathVariable Long id) {
        ganadoService.eliminarGanado(id);
        return "redirect:/ganado/lista";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        model.addAttribute("ganado", ganado);
        return "editar-ganado"; // nombre de tu vista HTML
    }

    // Guardar cambios del ganado
    @PostMapping("/actualizar")
    public String actualizarGanado(@ModelAttribute("ganado") Ganado ganado) {
        ganadoService.guardarGanado(ganado);
        return "redirect:/ganado/lista";
    }


}