package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.repository.VacunaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VacunaService {

    private final VacunaRepository vacunaRepository;

    public VacunaService(VacunaRepository vacunaRepository) {
        this.vacunaRepository = vacunaRepository;
    }

    public Vacuna guardarVacuna(Vacuna vacuna) {
        if (vacuna == null) throw new IllegalArgumentException("La vacuna no puede ser nula");
        if (vacuna.getNombre() == null || vacuna.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre de la vacuna es obligatorio");
        if (vacuna.getGanado() == null || vacuna.getGanado().getId() == null)
            throw new IllegalArgumentException("El ganado es obligatorio");

        if (vacuna.getFechaAplicacion() == null)
            vacuna.setFechaAplicacion(LocalDate.now());

        if (vacuna.getIntervaloCantidad() > 0 &&
                vacuna.getUnidadTiempo() != null &&
                !vacuna.getUnidadTiempo().trim().isEmpty()) {
            calcularProximaDosis(vacuna);
        }

        vacuna.setEstado(calcularEstado(vacuna));

        return vacunaRepository.saveAndFlush(vacuna);
    }

    public Vacuna actualizarVacuna(Long id, Vacuna vacunaActualizada) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacuna con ID " + id + " no encontrada"));

        if (vacunaActualizada.getNombre() != null && !vacunaActualizada.getNombre().trim().isEmpty())
            vacuna.setNombre(vacunaActualizada.getNombre());

        if (vacunaActualizada.getFechaAplicacion() != null)
            vacuna.setFechaAplicacion(vacunaActualizada.getFechaAplicacion());

        if (vacunaActualizada.getIntervaloCantidad() > 0)
            vacuna.setIntervaloCantidad(vacunaActualizada.getIntervaloCantidad());

        if (vacunaActualizada.getUnidadTiempo() != null &&
                !vacunaActualizada.getUnidadTiempo().trim().isEmpty())
            vacuna.setUnidadTiempo(vacunaActualizada.getUnidadTiempo());

        if (vacunaActualizada.getGanado() != null)
            vacuna.setGanado(vacunaActualizada.getGanado());

        calcularProximaDosis(vacuna);
        vacuna.setEstado(calcularEstado(vacuna));

        return vacunaRepository.saveAndFlush(vacuna);
    }



    private void calcularProximaDosis(Vacuna vacuna) {
        if (vacuna.getFechaAplicacion() == null ||
                vacuna.getIntervaloCantidad() <= 0 ||
                vacuna.getUnidadTiempo() == null ||
                vacuna.getUnidadTiempo().trim().isEmpty()) {
            return;
        }

        LocalDate fechaBase = vacuna.getFechaAplicacion();
        LocalDate proximaDosis;

        switch (vacuna.getUnidadTiempo().toLowerCase().trim()) {
            case "dias":
            case "día":
            case "días":
                proximaDosis = fechaBase.plusDays(vacuna.getIntervaloCantidad());
                break;
            case "semanas":
            case "semana":
                proximaDosis = fechaBase.plusWeeks(vacuna.getIntervaloCantidad());
                break;
            case "meses":
            case "mes":
                proximaDosis = fechaBase.plusMonths(vacuna.getIntervaloCantidad());
                break;
            case "años":
            case "año":
                proximaDosis = fechaBase.plusYears(vacuna.getIntervaloCantidad());
                break;
            default:
                return;
        }

        vacuna.setProximaDosis(proximaDosis);
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

    public void eliminar(Long id) {
        vacunaRepository.deleteById(id);
    }

    public List<Vacuna> obtenerHistorialPendiente() {
        return vacunaRepository.findVacunasPendientes();
    }

    private String calcularEstado(Vacuna vacuna) {
        LocalDate hoy = LocalDate.now();
        LocalDate proxima = vacuna.getProximaDosis();

        if (proxima == null) return "Sin fecha";
        if (hoy.isBefore(proxima)) {
            long dias = ChronoUnit.DAYS.between(hoy, proxima);
            return dias <= 7 ? "Próxima" : "Pendiente";
        } else if (hoy.isEqual(proxima)) {
            return "Aplicar hoy";
        } else {
            return "Vencida";
        }
    }
}
