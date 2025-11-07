package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.mode.Ganado;
import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GanadoService {

    private final GanadoRepository ganadoRepository;

    public GanadoService(GanadoRepository ganadoRepository) {
        this.ganadoRepository = ganadoRepository;
    }

    public List<Ganado> buscarPorRaza(String raza) {
        if (raza == null || raza.isBlank()) {
            return ganadoRepository.findAll();
        }
        return ganadoRepository.findByRazaContainingIgnoreCase(raza);
    }



    public Ganado guardarGanado(Ganado ganado) {
        if (ganado == null)
            throw new IllegalArgumentException("El objeto Ganado no puede ser nulo");

        if (ganado.getEstadoSalud() == null || ganado.getEstadoSalud().isBlank()) {
            ganado.setEstadoSalud("Saludable");
        }

        // Asignar unidad de tiempo de la edad automáticamente
        ganado.setUnidadTiempoEdad(calcularUnidadTiempo(ganado.getEdad()));

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
                .orElseThrow(() -> new RuntimeException("Ganado con ID " + id + " no encontrado"));

        ganadoExistente.setNombre(nuevoGanado.getNombre());
        ganadoExistente.setRaza(nuevoGanado.getRaza());
        ganadoExistente.setEdad(nuevoGanado.getEdad());
        ganadoExistente.setPeso(nuevoGanado.getPeso());
        ganadoExistente.setSexo(nuevoGanado.getSexo());

        if (nuevoGanado.getEstadoSalud() != null && !nuevoGanado.getEstadoSalud().isBlank()) {
            ganadoExistente.setEstadoSalud(nuevoGanado.getEstadoSalud());
        } else {
            ganadoExistente.setEstadoSalud("Saludable");
        }

        // Actualizar unidad de tiempo de la edad
        ganadoExistente.setUnidadTiempoEdad(calcularUnidadTiempo(nuevoGanado.getEdad()));

        return ganadoRepository.save(ganadoExistente);
    }

    public void eliminarGanado(Long id) {
        if (!ganadoRepository.existsById(id)) {
            throw new RuntimeException("Ganado con ID " + id + " no encontrado");
        }
        ganadoRepository.deleteById(id);
    }


    public String generarReporteDeLimites(Ganado ganado) {
        StringBuilder reporte = new StringBuilder();
        String raza = ganado.getRaza();
        String sexo = ganado.getSexo();

        if (raza == null || sexo == null) {
            return "Evaluación incompleta: se requieren los datos de raza y sexo para generar el informe.";
        }

        double pesoMax = obtenerPesoMaximoPorRaza(raza, sexo);
        double edadMax = obtenerEdadMaximaPorRaza(raza);

        boolean hayAdvertencias = false;

        // Evaluación de edad
        if (ganado.getEdad() > edadMax) {
            reporte.append("⚠️ Edad actual: ").append(ganado.getEdad())
                    .append(" años — supera el rango máximo estimado para la raza (")
                    .append(edadMax).append(" años). Se recomienda evaluar su condición general y productividad. ");
            hayAdvertencias = true;
        } else if (ganado.getEdad() > edadMax * 0.75) {
            reporte.append("ℹ️ Edad avanzada: ").append(ganado.getEdad())
                    .append(" años (límite estimado: ").append(edadMax).append(" años). Se sugiere seguimiento periódico. ");
            hayAdvertencias = true;
        }

        // Evaluación de peso
        if (ganado.getPeso() > pesoMax) {
            reporte.append("⚠️ Peso actual: ").append(ganado.getPeso())
                    .append(" kg — excede el máximo recomendado para la raza (")
                    .append(pesoMax).append(" kg). Podría existir sobrepeso, evaluar dieta y actividad física. ");
            hayAdvertencias = true;
        } else if (ganado.getPeso() > pesoMax * 0.9) {
            reporte.append("ℹ️ Peso elevado: ").append(ganado.getPeso())
                    .append(" kg (máximo sugerido: ").append(pesoMax).append(" kg). Mantener control nutricional. ");
            hayAdvertencias = true;
        }

        if (!hayAdvertencias) {
            return "✅ Evaluación satisfactoria: el ejemplar se encuentra dentro de los parámetros normales de edad y peso para su raza y sexo.";
        }

        return reporte.toString().trim();
    }



    public String generarRecomendacionNutricional(Ganado ganado) {
        double pesoMax = obtenerPesoMaximoPorRaza(ganado.getRaza(), ganado.getSexo());
        double pesoOptimo;
        String etapa;

        if (ganado.getEdad() <= 1.5) { etapa = "Joven en desarrollo"; pesoOptimo = pesoMax * 0.45; }
        else if (ganado.getEdad() <= 3) { etapa = "Crecimiento / producción inicial"; pesoOptimo = pesoMax * 0.75; }
        else { etapa = "Adulto en mantenimiento o reproducción"; pesoOptimo = pesoMax * 0.9; }

        StringBuilder rec = new StringBuilder("Etapa de vida: ").append(etapa).append(". ");

        double porcentaje = (ganado.getPeso() / pesoOptimo) * 100;
        double ingestaMS = ganado.getPeso() * 0.025;

        if (porcentaje < 90) {
            rec.append("Bajo peso: aumentar concentrado y forraje proteico. ");
        } else if (porcentaje > 110) {
            rec.append("Sobrepeso: reducir granos y aumentar forraje verde. ");
        } else {
            rec.append("Peso dentro del rango ideal. ");
        }

        rec.append("Consumo diario estimado de materia seca: ")
                .append(String.format("%.1f", ingestaMS)).append(" kg. ");

        if (ganado.getEdad() > 10) {
            rec.append("Suplementar calcio y fósforo por desgaste óseo. ");
        }

        return rec.toString().trim();
    }


    public String generarRecomendacionBienestar(Ganado ganado) {
        StringBuilder rec = new StringBuilder();

        if ("brahman".equalsIgnoreCase(ganado.getRaza())) {
            rec.append("Proporcionar sombra y baños antiparasitarios. ");
        }
        if ("holstein".equalsIgnoreCase(ganado.getRaza())) {
            rec.append("Mantener buena ventilación para evitar estrés térmico. ");
        }
        if (ganado.getEdad() < 1) {
            rec.append("Garantizar acceso constante a leche o sustituto lácteo. ");
        }
        if (ganado.getPeso() < 150) {
            rec.append("Proteger del frío y mantener cama seca. ");
        }

        rec.append("Revisar pezuñas cada tres meses y mantener registro sanitario actualizado.");

        return rec.toString().trim();
    }


    private double obtenerPesoMaximoPorRaza(String raza, String sexo) {
        if (raza == null || sexo == null) return 800;
        return switch (raza.toLowerCase()) {
            case "holstein" -> sexo.equals("macho") ? 1200 : 800;
            case "brahman" -> sexo.equals("macho") ? 1000 : 700;
            case "angus" -> sexo.equals("macho") ? 950 : 650;
            case "simmental" -> sexo.equals("macho") ? 1100 : 750;
            case "jersey" -> sexo.equals("macho") ? 800 : 550;
            case "hereford" -> sexo.equals("macho") ? 1000 : 700;
            case "limousin" -> sexo.equals("macho") ? 1100 : 750;
            case "charolais" -> sexo.equals("macho") ? 1200 : 850;
            default -> sexo.equals("macho") ? 900 : 650;
        };
    }

    private double obtenerEdadMaximaPorRaza(String raza) {
        if (raza == null) return 20;
        return switch (raza.toLowerCase()) {
            case "holstein", "jersey" -> 18;
            case "brahman" -> 22;
            case "angus", "hereford", "limousin" -> 20;
            case "simmental", "charolais" -> 21;
            default -> 20;
        };
    }


    public String calcularUnidadTiempo(double edad) {
        if (edad < 1.0) {
            double meses = edad * 12;
            return meses < 1 ? "días" : "meses";
        } else {
            return "años";
        }
    }

    public String formatoEdadParaVista(Double edadEnAnios) {
        if (edadEnAnios == null || edadEnAnios < 0) return "No registrada";
        final double edad = edadEnAnios;

        if (edad < 1.0) {
            double edadMeses = edad * 12;
            if (edadMeses < 1) {
                long dias = Math.round(edad * 365);
                return dias < 1 ? "Menos de un día" : dias + " día" + (dias != 1 ? "s" : "");
            } else {
                long meses = Math.round(edadMeses);
                return meses + " mes" + (meses != 1 ? "es" : "");
            }
        } else {
            return Math.floor(edad) == edad
                    ? String.format("%d año%s", Math.round(edad), edad != 1 ? "s" : "")
                    : String.format("%.1f año%s", edad, edad != 1 ? "s" : "");
        }




    }
}
