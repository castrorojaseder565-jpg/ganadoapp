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
import java.util.Optional;

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
                                @RequestParam("ganadoId") Long ganadoId,
                                @RequestParam(value = "nombreOtraVacuna", required = false) String nombreOtraVacuna,
                                RedirectAttributes redirectAttributes) {

        try {
            Ganado ganado = ganadoService.obtenerPorId(ganadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Ganado no encontrado con ID: " + ganadoId));
            vacuna.setGanado(ganado);

            Vacuna vacunaGuardada = vacunaService.guardarVacuna(vacuna, nombreOtraVacuna);

            redirectAttributes.addFlashAttribute("mensaje",
                    "✅ Vacuna '" + vacunaGuardada.getNombre() + "' registrada correctamente para el ganado '" +
                            ganado.getNombre() + "'.");

            return "redirect:/ganado/detalle/" + ganadoId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ Error al registrar vacuna: " + e.getMessage());
            return "redirect:/ganado/detalle/" + ganadoId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error inesperado al guardar la vacuna.");
            return "redirect:/ganado/detalle/" + ganadoId;
        }
    }


    @GetMapping("/editar/{id}")
    public String editarVacuna(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Vacuna> optVacuna = vacunaService.obtenerPorId(id);

        if (optVacuna.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la vacuna con ID " + id + ".");
            return "redirect:/vacuna/todas";
        }

        model.addAttribute("vacuna", optVacuna.get());
        model.addAttribute("ganados", ganadoService.listarGanados());
        return "vacuna-formulario-editar";
    }

    @PostMapping("/actualizar")
    public String actualizarVacuna(@ModelAttribute Vacuna vacunaActualizada,
                                   @RequestParam("ganadoId") Long ganadoId,
                                   @RequestParam(value = "nombreOtraVacuna", required = false) String nombreOtraVacuna,
                                   RedirectAttributes redirectAttributes) {

        try {
            Ganado ganado = ganadoService.obtenerPorId(ganadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Ganado no encontrado con ID: " + ganadoId));
            vacunaActualizada.setGanado(ganado);

            Vacuna actualizada = vacunaService.actualizarVacuna(vacunaActualizada.getId(), vacunaActualizada, nombreOtraVacuna);

            redirectAttributes.addFlashAttribute("mensaje",
                    "✅ Vacuna '" + actualizada.getNombre() + "' actualizada correctamente.");
            return "redirect:/ganado/detalle/" + ganadoId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ " + e.getMessage());
            return "redirect:/vacuna/editar/" + vacunaActualizada.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error inesperado al actualizar la vacuna.");
            return "redirect:/ganado/detalle/" + ganadoId;
        }
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarVacuna(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Vacuna> vacunaOpt = vacunaService.obtenerPorId(id);

        if (vacunaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la vacuna a eliminar.");
            return "redirect:/vacuna/todas";
        }

        Vacuna vacuna = vacunaOpt.get();
        Long ganadoId = vacuna.getGanado().getId();

        try {
            vacunaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje",
                    "🗑️ Vacuna '" + vacuna.getNombre() + "' eliminada correctamente.");
            return "redirect:/ganado/detalle/" + ganadoId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ No se pudo eliminar la vacuna: " + e.getMessage());
            return "redirect:/ganado/detalle/" + ganadoId;
        }
    }

    @GetMapping("/todas")
    public String mostrarTodasLasVacunas(Model model) {
        List<Vacuna> todas = vacunaService.obtenerTodas();
        model.addAttribute("vacunas", todas);
        return "vacunas-todas";
    }
}
