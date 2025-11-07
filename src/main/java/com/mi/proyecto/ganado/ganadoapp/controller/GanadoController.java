package com.mi.proyecto.ganado.ganadoapp.controller;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ganado")
public class GanadoController {

    @Autowired
    private GanadoService ganadoService;

    @Autowired
    private VacunaService vacunaService;

    @GetMapping("/nuevo")
    public String mostrarFormularioGanado(Model model) {
        if (!model.containsAttribute("ganado")) {
            model.addAttribute("ganado", new Ganado());
        }
        return "registro-ganado";
    }

    @GetMapping("/lista")
    public String listarGanado(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        List<Ganado> listaGanado = ganadoService.buscarPorRaza(buscar);
        model.addAttribute("listaGanado", listaGanado);
        model.addAttribute("filtro", buscar); // Para mantener el texto en la caja de búsqueda
        return "lista-ganado";
    }


    @GetMapping("/detalle/{id}")
    public String detalleGanado(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Ganado> optGanado = ganadoService.obtenerPorId(id);
        if (optGanado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el ganado con ID " + id + ".");
            return "redirect:/ganado/lista";
        }

        Ganado ganado = optGanado.get();


        List<Vacuna> vacunasRegistradas = vacunaService.obtenerVacunasPorGanado(id);
        if (vacunasRegistradas == null) vacunasRegistradas = List.of(); // Evita NullPointerException

        // Recomendaciones dinámicas
        String recomendacionVacunas = vacunaService.generarRecomendacionPorRaza(ganado.getRaza());
        if (recomendacionVacunas == null) recomendacionVacunas = "";

        String reporteLimites = ganadoService.generarReporteDeLimites(ganado);
        if (reporteLimites == null) reporteLimites = "";

        String recomendacionNutricional = ganadoService.generarRecomendacionNutricional(ganado);
        if (recomendacionNutricional == null) recomendacionNutricional = "";

        String recomendacionBienestar = ganadoService.generarRecomendacionBienestar(ganado);
        if (recomendacionBienestar == null) recomendacionBienestar = "";


        String edadFormateada = ganadoService.formatoEdadParaVista(ganado.getEdad());

        // Se agregan los datos y reportes al modelo
        model.addAttribute("ganado", ganado);
        model.addAttribute("vacunas", vacunasRegistradas);
        model.addAttribute("reporteLimites", reporteLimites);
        model.addAttribute("recomendacionNutricional", recomendacionNutricional);
        model.addAttribute("recomendacionBienestar", recomendacionBienestar);
        model.addAttribute("recomendacionVacuna", recomendacionVacunas);
        model.addAttribute("edadFormateada", edadFormateada);

        return "detalle-ganado";
    }


    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Ganado> optGanado = ganadoService.obtenerPorId(id);
        if (optGanado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el ganado con ID " + id + ".");
            return "redirect:/ganado/lista";
        }
        model.addAttribute("ganado", optGanado.get());
        return "editar-ganado";
    }



    @PostMapping("/guardar")
    public String guardarGanado(@ModelAttribute Ganado ganado,
                                @RequestParam(value = "otraRaza", required = false) String otraRaza,
                                RedirectAttributes redirectAttributes) {

        if ("Otros".equalsIgnoreCase(ganado.getRaza()) && otraRaza != null && !otraRaza.trim().isEmpty()) {
            ganado.setRaza(otraRaza.trim());
        }

        // Validaciones
        if (ganado.getNombre() == null || ganado.getNombre().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del ganado es obligatorio.");
            redirectAttributes.addFlashAttribute("ganado", ganado);
            return "redirect:/ganado/nuevo";
        }
        if (ganado.getRaza() == null || ganado.getRaza().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe especificar la raza del ganado.");
            redirectAttributes.addFlashAttribute("ganado", ganado);
            return "redirect:/ganado/nuevo";
        }
        if (ganado.getSexo() == null || ganado.getSexo().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar el sexo del ganado.");
            redirectAttributes.addFlashAttribute("ganado", ganado);
            return "redirect:/ganado/nuevo";
        }
        if (ganado.getPeso() <= 0 || ganado.getPeso() > 2000) {
            redirectAttributes.addFlashAttribute("error", "El peso debe estar entre 0.1 y 2000 kg.");
            redirectAttributes.addFlashAttribute("ganado", ganado);
            return "redirect:/ganado/nuevo";
        }

        try {
            Ganado ganadoGuardado = ganadoService.guardarGanado(ganado);
            redirectAttributes.addFlashAttribute("mensaje",
                    "El ganado '" + ganadoGuardado.getNombre() + "' fue registrado correctamente.");
            return "redirect:/ganado/detalle/" + ganadoGuardado.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error al guardar el registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("ganado", ganado);
            return "redirect:/ganado/nuevo";
        }
    }

    @PostMapping("/actualizar")
    public String actualizarGanado(@ModelAttribute("ganado") Ganado ganado,
                                   @RequestParam(value = "otraRaza", required = false) String otraRaza,
                                   RedirectAttributes redirectAttributes) {

        if ("Otros".equalsIgnoreCase(ganado.getRaza()) && otraRaza != null && !otraRaza.trim().isEmpty()) {
            ganado.setRaza(otraRaza.trim());
        }

        if (ganado.getPeso() <= 0 || ganado.getPeso() > 2000) {
            redirectAttributes.addFlashAttribute("error",
                    "El peso ingresado no es válido. Debe estar entre 0.1 y 2000 kg.");
            return "redirect:/ganado/editar/" + ganado.getId();
        }

        try {
            ganadoService.actualizarGanado(ganado.getId(), ganado);
            redirectAttributes.addFlashAttribute("mensaje", "Los datos del ganado se actualizaron correctamente.");
            return "redirect:/ganado/detalle/" + ganado.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo actualizar el registro: " + e.getMessage());
            return "redirect:/ganado/editar/" + ganado.getId();
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGanado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ganadoService.eliminarGanado(id);
            redirectAttributes.addFlashAttribute("mensaje", "El registro del ganado fue eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el registro: " + e.getMessage());
        }
        return "redirect:/ganado/lista";
    }
}
