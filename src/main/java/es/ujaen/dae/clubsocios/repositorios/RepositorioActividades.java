package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RepositorioActividades {
    private List<Actividad> actividades;
    private int contadorIds = 1;

    private int generarId() {
        return contadorIds++;
    }

    /**
     * @brief Constructor por defecto de la clase RepositorioActividades
     */
    public RepositorioActividades() {
        actividades = new LinkedList<>();
    }

    /**
     * @param actividad actividad a crear
     * @brief Crea una nueva actividad
     */
    public void crear(Actividad actividad) {
        actividad.fechasValidas();
        actividad.setId(generarId());
        actividades.add(actividad);
    }
}
