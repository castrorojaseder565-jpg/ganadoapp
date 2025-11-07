package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VacunaRepository extends JpaRepository<Vacuna, Long> {

    // Obtener vacunas asociadas a un ganado específico
    List<Vacuna> findByGanadoId(Long ganadoId);

    // Obtener vacunas pendientes o próximas a vencer
    @Query("SELECT v FROM Vacuna v WHERE v.estado = 'Pendiente' OR v.proximaDosis <= CURRENT_DATE")
    List<Vacuna> findVacunasPendientes();

    //Obtener historial de vacunas por nombre y raza
    @Query("SELECT v FROM Vacuna v WHERE LOWER(v.nombre) = LOWER(:nombre) AND LOWER(v.ganado.raza) = LOWER(:raza)")
    List<Vacuna> findByNombreAndGanadoRaza(@Param("nombre") String nombre, @Param("raza") String raza);

    // Obtener todas las vacunas por raza (para recomendaciones generales)
    @Query("SELECT v FROM Vacuna v WHERE LOWER(v.ganado.raza) = LOWER(:raza)")
    List<Vacuna> findByGanadoRaza(@Param("raza") String raza);

    //Verificar si ya existe una vacuna para un ganado en una fecha específica
    boolean existsByNombreAndGanadoIdAndFechaAplicacion(String nombre, Long id, LocalDate fechaAplicacion);

    //Calcular el promedio de días entre aplicación y próxima dosis por tipo y raza
    @Query("SELECT AVG(DATEDIFF(v.proximaDosis, v.fechaAplicacion)) " +
            "FROM Vacuna v WHERE LOWER(v.nombre) = LOWER(:nombre) AND LOWER(v.ganado.raza) = LOWER(:raza)")
    Optional<Double> promedioDiasPorTipoYRaza(@Param("nombre") String nombre, @Param("raza") String raza);

    //Obtener las vacunas que vencen en los próximos X días (para alertas)
    @Query("SELECT v FROM Vacuna v WHERE v.proximaDosis BETWEEN CURRENT_DATE AND :fechaLimite")
    List<Vacuna> findVacunasPorVencer(@Param("fechaLimite") LocalDate fechaLimite);

    //(Opcional) Vacunas recientes — evitar recomendar duplicadas
    @Query("SELECT v FROM Vacuna v WHERE v.ganado.id = :ganadoId AND v.nombre = :nombre AND v.fechaAplicacion >= :fechaLimite")
    List<Vacuna> findVacunasRecientes(@Param("ganadoId") Long ganadoId,
                                      @Param("nombre") String nombre,
                                      @Param("fechaLimite") LocalDate fechaLimite);
}
