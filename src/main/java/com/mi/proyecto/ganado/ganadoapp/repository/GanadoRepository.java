package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GanadoRepository extends JpaRepository<Ganado, Long> {



    //Cuenta el número de ganado por sexo
    long countBySexo(String sexo);

    //Cuenta el número de ganado por estado de salud
    long countByEstadoSalud(String estadoSalud);



    //Busca ganado cuya raza contenga la cadena dada, sin importar mayúsculas/minúsculas
    List<Ganado> findByRazaContainingIgnoreCase(String raza);



    //Calcula el peso promedio de todo el ganado
    @Query("SELECT AVG(g.peso) FROM Ganado g")
    Double promedioPeso();

    // Calcula la edad promedio de todo el ganado
    @Query("SELECT AVG(g.edad) FROM Ganado g")
    Double promedioEdad();

    //Obtiene la distribución del conteo de ganado por raza (Raza, Cantidad)
    @Query("SELECT g.raza, COUNT(g) FROM Ganado g GROUP BY g.raza ORDER BY COUNT(g) DESC")
    List<Object[]> countByRaza();

}