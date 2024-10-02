package src.main.java.es.ujaen.dae.clubsocios.entidades;

import java.util.HashMap;

public class Temporada {
    private int anio;
    private HashMap<String, Actividad> actividades;

    public Temporada(int anio) {
        this.anio = anio;
        actividades = new HashMap<String, Actividad>();
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
}
