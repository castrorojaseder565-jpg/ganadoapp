package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GanadoRepository extends JpaRepository<Ganado, Long> {

    long countBySexo(String sexo);
    long countByEstadoSalud(String estadoSalud);

    @Query("SELECT AVG(g.peso) FROM Ganado g")
    Double promedioPeso();
}
//  countBySexo: cuenta la cantidad de ganado según el sexo (Macho/Hembra).