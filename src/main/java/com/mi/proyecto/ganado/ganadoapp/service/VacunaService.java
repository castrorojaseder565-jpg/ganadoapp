package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.repository.VacunaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class VacunaService {

    private final VacunaRepository vacunaRepository;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VacunaService(VacunaRepository vacunaRepository) {
        this.vacunaRepository = vacunaRepository;
    }


    public Vacuna guardarVacuna(Vacuna vacuna, String nombreOtraVacuna) {
        if (vacuna == null) throw new IllegalArgumentException("La vacuna no puede ser nula.");

        corregirNombreVacunaSiEsOtro(vacuna, nombreOtraVacuna);
        validarVacuna(vacuna);

        if (vacuna.getFechaAplicacion() == null)
            vacuna.setFechaAplicacion(LocalDate.now());

        if (vacunaRepository.existsByNombreAndGanadoIdAndFechaAplicacion(
                vacuna.getNombre(), vacuna.getGanado().getId(), vacuna.getFechaAplicacion())) {
            throw new IllegalArgumentException("Ya existe una vacuna con ese nombre para este ganado en la misma fecha.");
        }

        calcularProximaDosis(vacuna);
        vacuna.setEstado(calcularEstado(vacuna));

        return vacunaRepository.saveAndFlush(vacuna);
    }

    public Vacuna actualizarVacuna(Long id, Vacuna vacunaActualizada, String nombreOtraVacuna) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacuna no encontrada."));

        corregirNombreVacunaSiEsOtro(vacunaActualizada, nombreOtraVacuna);

        if (vacunaActualizada.getNombre() != null && !vacunaActualizada.getNombre().isBlank())
            vacuna.setNombre(vacunaActualizada.getNombre());

        if (vacunaActualizada.getFechaAplicacion() != null)
            vacuna.setFechaAplicacion(vacunaActualizada.getFechaAplicacion());

        if (vacunaActualizada.getIntervaloCantidad() > 0)
            vacuna.setIntervaloCantidad(vacunaActualizada.getIntervaloCantidad());

        if (vacunaActualizada.getUnidadTiempo() != null && !vacunaActualizada.getUnidadTiempo().isBlank())
            vacuna.setUnidadTiempo(vacunaActualizada.getUnidadTiempo());

        calcularProximaDosis(vacuna);
        vacuna.setEstado(calcularEstado(vacuna));

        return vacunaRepository.saveAndFlush(vacuna);
    }





    public String generarRecomendacionPorRaza(String raza) {
        if (raza == null || raza.isEmpty()) {
            return "No hay recomendaciones específicas por raza.";
        }

        raza = raza.toLowerCase();

        return switch (raza) {
            case "holstein" -> "• Vacuna contra mastitis (S. aureus) → cada 6 meses.\n" +
                    "• Control estricto de IBR y BVD.\n" +
                    "• Suplementar con minerales y vitaminas para evitar inmunodeficiencias.";
            case "brahman" -> "• Refuerzo de carbunco y leptospirosis anual.\n" +
                    "• Desparasitación preventiva cada 2-3 meses.\n" +
                    "• Control especial de fiebre aftosa por alta exposición al pastoreo.";
            case "normando" -> "• Refuerzo anual de IBR/BVD.\n" +
                    "• Vacuna anticlostridial doble en zonas húmedas.\n" +
                    "• Control respiratorio en climas fríos.";
            case "jersey" -> "• Control de mastitis y fiebre aftosa estricto.\n" +
                    "• IBR y BVD cada 6 meses.\n" +
                    "• Refuerzo de carbunco anual.";
            case "angus" -> "• Vacuna de carbunco anual.\n" +
                    "• Refuerzo de leptospirosis cada 8 meses.\n" +
                    "• Control antiparasitario reforzado.";
            default -> "• Mantener calendario base y controles veterinarios regulares.";
        };
    }

    public String generarRecomendacionVacunas(Ganado ganado) {
        if (ganado == null) {
            return "<em>No se pudo generar recomendación: datos del ganado no disponibles.</em>";
        }

        String raza = Optional.ofNullable(ganado.getRaza()).orElse("Desconocida");
        double edad = ganado.getEdad();
        String sexo = Optional.ofNullable(ganado.getSexo()).orElse("No definido");

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='recomendacion'>");
        sb.append("<strong>Plan Sanitario Profesional para ").append(raza).append(":</strong><br><br>");

        // 1️⃣ Vacunas Generales
        sb.append("<u>Vacunas Generales:</u><br>");
        sb.append("• Fiebre Aftosa → cada 6 meses.<br>");
        sb.append("• Carbunco → anual.<br>");
        sb.append("• Leptospirosis → cada 6-12 meses.<br>");
        sb.append("• Clostridiosis → iniciar 4-6 meses, refuerzo anual.<br>");
        sb.append("• Rabia bovina → anual en zonas endémicas.<br>");
        sb.append("• Pasteurelosis → anual, especialmente en zonas húmedas.<br>");
        sb.append("• Control de parasitos → cada 3-4 meses.<br><br>");

        // 2️⃣ Vacunas según sexo
        sb.append("<u>Vacunas según Sexo:</u><br>");
        if (sexo.equalsIgnoreCase("Hembra")) {
            sb.append("• Brucelosis → dosis única 3-8 meses.<br>");
            sb.append("• IBR/BVD/PI3 → refuerzos semestrales para hembras reproductoras.<br>");
            sb.append("• Vacuna reproductiva (campylobacteriosis/tricomoniasis) → antes del servicio.<br>");
            sb.append("• Mastitis → semestral si es lechera.<br><br>");
        } else if (sexo.equalsIgnoreCase("Macho")) {
            sb.append("• IBR/BVD/PI3 → anual o semestral si reproductor.<br>");
            sb.append("• Leptospirosis → refuerzo semestral si monta natural.<br>");
            sb.append("• Carbunco → anual.<br><br>");
        }

        // 3️⃣ Vacunas según edad
        sb.append("<u>Vacunas según Edad:</u><br>");
        if (edad < 1) {
            sb.append("• Terneros: anticlostridial 4-6 meses, refuerzo a los 30 días.<br>");
            sb.append("• Pasteurelosis: dosis inicial + refuerzo a 30 días.<br>");
            sb.append("• Diarrea neonatal → según riesgo.<br><br>");
        } else if (edad >= 1 && edad < 3) {
            sb.append("• Refuerzos de aftosa, leptospirosis y rabia cada 6 meses.<br>");
            sb.append("• Clostridiosis anual.<br><br>");
        } else if (edad >= 3 && edad < 6) {
            sb.append("• Vacuna reproductiva anual.<br>");
            sb.append("• Clostridiosis y leptospirosis anuales.<br><br>");
        } else {
            sb.append("• Refuerzos generales anuales.<br>");
            sb.append("• Control antiparasitario cada 3-4 meses.<br>");
            sb.append("• Refuerzo contra carbunco y rabia.<br><br>");
        }

        // 4️⃣ Recomendaciones por raza
        sb.append("<u>Recomendaciones según Raza:</u><br>");
        sb.append(generarRecomendacionPorRaza(raza).replace("\n", "<br>"));

        // Nota ética profesional
        sb.append("<br><em>Nota: El calendario puede variar según la zona, clima y condiciones del hato. Consulte siempre al veterinario de confianza para ajustes individuales.</em>");
        sb.append("</div>");

        return sb.toString();
    }






    private void corregirNombreVacunaSiEsOtro(Vacuna vacuna, String nombreOtraVacuna) {
        if ("Otros".equalsIgnoreCase(vacuna.getNombre()) &&
                nombreOtraVacuna != null && !nombreOtraVacuna.trim().isEmpty()) {
            vacuna.setNombre(nombreOtraVacuna.trim());
        }
    }



    private void calcularProximaDosis(Vacuna vacuna) {
        if (vacuna.getFechaAplicacion() == null || vacuna.getIntervaloCantidad() <= 0 ||
                vacuna.getUnidadTiempo() == null) return;

        LocalDate base = vacuna.getFechaAplicacion();
        String unidad = vacuna.getUnidadTiempo().toLowerCase().trim();

        switch (unidad) {
            case "día", "días", "dias" -> vacuna.setProximaDosis(base.plusDays(vacuna.getIntervaloCantidad()));
            case "semana", "semanas" -> vacuna.setProximaDosis(base.plusWeeks(vacuna.getIntervaloCantidad()));
            case "mes", "meses" -> vacuna.setProximaDosis(base.plusMonths(vacuna.getIntervaloCantidad()));
            case "año", "años" -> vacuna.setProximaDosis(base.plusYears(vacuna.getIntervaloCantidad()));
        }
    }

    private String calcularEstado(Vacuna vacuna) {
        LocalDate hoy = LocalDate.now();
        LocalDate proxima = vacuna.getProximaDosis();

        if (proxima == null) return "Sin fecha";
        long dias = ChronoUnit.DAYS.between(hoy, proxima);

        if (hoy.isEqual(proxima)) return "Aplicar hoy";
        if (dias > 7) return "Pendiente";
        if (dias > 0) return "Próxima";
        return "Vencida";
    }

    private void validarVacuna(Vacuna vacuna) {
        if (vacuna.getNombre() == null || vacuna.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la vacuna es obligatorio.");
        if (vacuna.getGanado() == null || vacuna.getGanado().getId() == null)
            throw new IllegalArgumentException("Debe seleccionar un ganado.");
    }



    public List<Vacuna> obtenerTodas() {
        List<Vacuna> vacunas = vacunaRepository.findAll();
        vacunas.forEach(v -> v.setEstado(calcularEstado(v)));
        return vacunas;
    }

    public Optional<Vacuna> obtenerPorId(Long id) {
        Optional<Vacuna> vacuna = vacunaRepository.findById(id);
        vacuna.ifPresent(v -> v.setEstado(calcularEstado(v)));
        return vacuna;
    }

    public List<Vacuna> obtenerVacunasPorGanado(Long ganadoId) {
        List<Vacuna> vacunas = vacunaRepository.findByGanadoId(ganadoId);
        vacunas.forEach(v -> v.setEstado(calcularEstado(v)));
        return vacunas;
    }

    public void eliminar(Long id) {
        vacunaRepository.deleteById(id);
    }
}
