package com.mi.proyecto.ganado.ganadoapp.mode;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ganado")
public class Ganado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String raza;

    @Column(nullable = false)
    private double peso; // Peso en kilogramos

    @Column(nullable = false)
    private double edad; // Valor numérico de la edad

    @Column(name = "unidad_tiempo_edad", length = 20, nullable = false)
    private String unidadTiempoEdad; // "días", "meses" o "años"

    @Column(nullable = false, length = 20)
    private String sexo; // Ejemplo: "Macho", "Hembra"

    @Column(name = "estado_salud", length = 50, nullable = false)
    private String estadoSalud;

    @OneToMany(mappedBy = "ganado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vacuna> vacunas = new ArrayList<>();

    public Ganado() {}

    public Ganado(String nombre, String raza, double peso, double edad, String unidadTiempoEdad, String sexo, String estadoSalud) {
        this.nombre = nombre;
        this.raza = raza;
        this.peso = peso;
        this.edad = edad;
        this.unidadTiempoEdad = unidadTiempoEdad;
        this.sexo = sexo;
        this.estadoSalud = estadoSalud;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getEdad() { return edad; }
    public void setEdad(double edad) { this.edad = edad; }

    public String getUnidadTiempoEdad() { return unidadTiempoEdad; }
    public void setUnidadTiempoEdad(String unidadTiempoEdad) { this.unidadTiempoEdad = unidadTiempoEdad; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getEstadoSalud() { return estadoSalud; }
    public void setEstadoSalud(String estadoSalud) { this.estadoSalud = estadoSalud; }

    public List<Vacuna> getVacunas() { return vacunas; }
    public void setVacunas(List<Vacuna> vacunas) { this.vacunas = vacunas; }

    // Métodos utilitarios
    public void addVacuna(Vacuna vacuna) {
        vacunas.add(vacuna);
        vacuna.setGanado(this);
    }

    public void removeVacuna(Vacuna vacuna) {
        vacunas.remove(vacuna);
        vacuna.setGanado(null);
    }

    @Override
    public String toString() {
        return "Ganado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", raza='" + raza + '\'' +
                ", peso=" + peso +
                ", edad=" + edad + " " + unidadTiempoEdad +
                ", sexo='" + sexo + '\'' +
                ", estadoSalud='" + estadoSalud + '\'' +
                '}';
    }
}
