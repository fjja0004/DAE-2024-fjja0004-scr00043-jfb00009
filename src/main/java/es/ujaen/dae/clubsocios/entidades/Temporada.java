package es.ujaen.dae.clubsocios.entidades;

import jakarta.validation.constraints.Positive;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Temporada {
    @Positive
    private int anio;
    private List<Actividad> actividades;

    public Temporada(int anio) {
        this.anio = anio;
        actividades = new LinkedList<>();
    }

    public int getAnio() {
        return anio;
    }

    public void anadirNuevaActividad(Actividad actividad) {
        actividades.add(actividad);
    }
}
