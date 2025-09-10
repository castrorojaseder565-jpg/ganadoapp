package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.mode.Vacuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VacunaRepository extends JpaRepository<Vacuna, Long> {
    List<Vacuna> findByGanadoId(Long ganadoId);

    @Query("SELECT v FROM Vacuna v WHERE v.estado = 'Pendiente' OR v.proximaDosis <= CURRENT_DATE")
    List<Vacuna> findVacunasPendientes();

}
