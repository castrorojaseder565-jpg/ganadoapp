package com.mi.proyecto.ganado.ganadoapp.controller;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;


@Controller
@RequestMapping("/vacuna")
public class VacunaController {

    private final VacunaService vacunaService;
    private final GanadoService ganadoService;

    public VacunaController(VacunaService vacunaService, GanadoService ganadoService) {
        this.vacunaService = vacunaService;
        this.ganadoService = ganadoService;
    }

    @GetMapping("/registrar")
    public String mostrarFormularioVacuna(Model model) {
        model.addAttribute("vacuna", new Vacuna());
        model.addAttribute("ganados", ganadoService.listarGanados());
        return "vacuna-formulario";
    }


    @PostMapping("/guardar")
    public String guardarVacuna(@ModelAttribute Vacuna vacuna,
                                @RequestParam("ganadoId") Long ganadoId) {
        // Asociar el ganado al que pertenece la vacuna (solo con el ID)
        Ganado ganado = new Ganado();
        ganado.setId(ganadoId);
        vacuna.setGanado(ganado);

        // Guardar usando el servicio
        vacunaService.guardarVacuna(vacuna);

        // Redirigir al listado de ganado u otra vista
        return "redirect:/ganado/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVacuna(@PathVariable Long id) {
        Vacuna vacuna = vacunaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con id: " + id));

        Long ganadoId = vacuna.getGanado().getId();
        vacunaService.eliminar(id);

        return "redirect:/ganado/detalle/" + ganadoId;
    }

    @GetMapping("/editar/{id}")
    public String editarVacuna(@PathVariable Long id, Model model) {
        Vacuna vacuna = vacunaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con id: " + id));

        List<Ganado> ganados = ganadoService.listarGanados();

        model.addAttribute("vacuna", vacuna);
        model.addAttribute("ganados", ganados);

        return "vacuna-formulario-editar";
    }

    @PostMapping("/actualizar")
    public String actualizarVacuna(@ModelAttribute Vacuna vacunaActualizada,
                                   @RequestParam Long ganadoId,
                                   RedirectAttributes redirectAttributes) {
        Ganado ganado = ganadoService.obtenerPorId(ganadoId)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado con id: " + ganadoId));

        vacunaActualizada.setGanado(ganado);

        Vacuna vacunaGuardada = vacunaService.actualizarVacuna(
                vacunaActualizada.getId(), vacunaActualizada);

        redirectAttributes.addFlashAttribute("mensaje",
                "Vacuna '" + vacunaGuardada.getNombre() + "' actualizada exitosamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        return "redirect:/ganado/detalle/" + ganadoId;
    }


    @GetMapping("/todas")
    public String mostrarTodasLasVacunas(Model model) {
        List<Vacuna> todas = vacunaService.obtenerTodas();
        model.addAttribute("vacunas", todas);
        return "vacunas-todas";
    }
}