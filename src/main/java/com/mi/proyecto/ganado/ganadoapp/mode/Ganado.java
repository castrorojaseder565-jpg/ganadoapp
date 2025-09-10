package com.mi.proyecto.ganado.ganadoapp.mode;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Ganado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String raza;
    private int edad;
    private double peso;
    private String sexo;
    private String estadoSalud;



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

    public String getRaza() {

        return raza;
    }

    public void setRaza(String raza) {

        this.raza = raza;
    }

    public int getEdad() {

        return edad;
    }

    public void setEdad(int edad) {

        this.edad = edad;
    }

    public double getPeso() {

        return peso;
    }

    public void setPeso(double peso) {

        this.peso = peso;
    }

    public String getSexo() {

        return sexo;
    }

    public void setSexo(String sexo) {

        this.sexo = sexo;
    }

    public String getEstadoSalud() {

        return estadoSalud;
    }

    public void setEstadoSalud(String estadoSalud) {

        this.estadoSalud = estadoSalud;
    }
    public List<Vacuna> getVacunas() {
        return vacunas; }

    public void setVacunas(List<Vacuna> vacunas) {
        this.vacunas = vacunas; }


    @OneToMany(mappedBy = "ganado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vacuna> vacunas = new ArrayList<>();



}
