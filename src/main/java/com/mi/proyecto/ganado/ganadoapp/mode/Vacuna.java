package com.mi.proyecto.ganado.ganadoapp.mode;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "vacunas") // Especificar nombre de tabla explícitamente
public class Vacuna {


    @ManyToOne(fetch = FetchType.LAZY) //  LAZY para mejor rendimiento
    @JoinColumn(name = "ganado_id", nullable = false)
    private Ganado ganado;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) // Hacer el nombre obligatorio
    private String nombre;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_aplicacion")
    private LocalDate fechaAplicacion;



    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;



    @Column(name = "intervalo_cantidad")
    private int intervaloCantidad;

    // Unidad del intervalo: "dias","semanas","meses"
    @Column(name = "unidad_tiempo", length = 20)
    private String unidadTiempo;

    // Estado opcional: "pendiente", "completada", etc.
    @Column(length = 50)
    private String estado;




    public Vacuna() {}

    public Vacuna(String nombre, Ganado ganado) {
        this.nombre = nombre;
        this.ganado = ganado;
        this.fechaAplicacion = LocalDate.now();
    }


    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getNombre() {

        return nombre;
    }

    public void setNombre(String nombre) {

        this.nombre = nombre;
    }

    public LocalDate getFechaAplicacion() {

        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {

        this.fechaAplicacion = fechaAplicacion;
    }

    public LocalDate getProximaDosis() {

        return proximaDosis;
    }

    public void setProximaDosis(LocalDate proximaDosis) {

        this.proximaDosis = proximaDosis;
    }

    public int getIntervaloCantidad() {

        return intervaloCantidad;
    }

    public void setIntervaloCantidad(int intervaloCantidad) {

        this.intervaloCantidad = intervaloCantidad;
    }

    public String getUnidadTiempo() {

        return unidadTiempo;
    }

    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = unidadTiempo;
    }

    public String getEstado() {

        return estado;
    }

    public void setEstado(String estado) {

        this.estado = estado;
    }

    public Ganado getGanado() {

        return ganado;
    }

    public void setGanado(Ganado ganado) {

        this.ganado = ganado;
    }

    @Transient
    public String getEstadoCalculado() {
        if (proximaDosis == null) return "Sin fecha";

        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(proximaDosis)) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, proximaDosis);
            if (dias <= 7) return "Próxima (menos de 7 días)";
            return "Pendiente";
        } else if (hoy.isEqual(proximaDosis)) {
            return "Aplicar hoy";
        } else {
            return "Vencida";
        }
    }


    @Override
    public String toString() {
        return "Vacuna{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fechaAplicacion=" + fechaAplicacion +
                ", proximaDosis=" + proximaDosis +
                ", intervaloCantidad=" + intervaloCantidad +
                ", unidadTiempo='" + unidadTiempo + '\'' +
                ", estado='" + estado + '\'' +
                ", ganadoId=" + (ganado != null ? ganado.getId() : "null") +
                '}';
    }
}