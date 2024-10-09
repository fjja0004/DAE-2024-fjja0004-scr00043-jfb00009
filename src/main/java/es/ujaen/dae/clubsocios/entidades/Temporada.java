package es.ujaen.dae.clubsocios.entidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Temporada {
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
