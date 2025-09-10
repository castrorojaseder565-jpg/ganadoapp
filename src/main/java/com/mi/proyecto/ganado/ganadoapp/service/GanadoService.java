package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GanadoService {

    @Autowired
    private GanadoRepository ganadoRepository;

    public Ganado guardarGanado(Ganado ganado) {
        return ganadoRepository.save(ganado);
    }

    public List<Ganado> listarGanados() {
        return ganadoRepository.findAll();
    }

    public Optional<Ganado> obtenerPorId(Long id) {
        return ganadoRepository.findById(id);
    }

    public Ganado actualizarGanado(Long id, Ganado nuevoGanado) {
        Ganado ganadoExistente = ganadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ganado con ID " + id + " no encontrado."));

        ganadoExistente.setNombre(nuevoGanado.getNombre());
        ganadoExistente.setRaza(nuevoGanado.getRaza());
        ganadoExistente.setEdad(nuevoGanado.getEdad());
        ganadoExistente.setPeso(nuevoGanado.getPeso());
        ganadoExistente.setSexo(nuevoGanado.getSexo());
        ganadoExistente.setEstadoSalud(nuevoGanado.getEstadoSalud());

        return ganadoRepository.save(ganadoExistente);
    }

    public void eliminarGanado(Long id) {
        ganadoRepository.deleteById(id);
    }
}
