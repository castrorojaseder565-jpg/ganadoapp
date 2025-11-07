package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private final GanadoRepository ganadoRepository;

    public EstadisticasService(GanadoRepository ganadoRepository) {
        this.ganadoRepository = ganadoRepository;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> datos = new HashMap<>();

        //Métricas Simples y Conteo
        Long totalGanado = ganadoRepository.count();
        datos.put("totalGanado", totalGanado);

        //Peso promedio
        Double pesoPromedio = ganadoRepository.promedioPeso();
        datos.put("promedioPeso", pesoPromedio != null ? pesoPromedio : 0.0);

        //Conteo por Sexo
        datos.put("machos", ganadoRepository.countBySexo("Macho"));
        datos.put("hembras", ganadoRepository.countBySexo("Hembra"));

        //Conteo por Estado de Salud (solo los existentes)
        datos.put("saludables", ganadoRepository.countByEstadoSalud("Saludable"));
        datos.put("enfermos", ganadoRepository.countByEstadoSalud("Enfermo"));

        //Distribución por Raza
        List<Object[]> razaCounts = ganadoRepository.countByRaza();
        Map<String, Long> porRaza = razaCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
        datos.put("porRaza", porRaza);

        //Distribución por Estado de Salud
        Map<String, Long> porEstadoSalud = new LinkedHashMap<>();
        porEstadoSalud.put("Saludable", (Long) datos.get("saludables"));
        porEstadoSalud.put("Enfermo", (Long) datos.get("enfermos"));

        datos.put("porEstadoSalud", porEstadoSalud);

        return datos;
    }
}
