package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.LinkedList;
import java.util.List;

public class Temporada {
    @Positive
    private int anio;
    private List<Actividad> actividades;

    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        actividades = new LinkedList<>();
    }

    /**
     * @param anio año de la temporada
     * @brief Constructor parametrizado de la clase Temporada
     */
    public Temporada(int anio) {
        this.anio = anio;
        actividades = new LinkedList<>();
    }

    /**
     * @param actividad actividad a crear
     * @brief Crear una actividad, si es válida
     */
    public void crearActividad(@Valid Actividad actividad) {

        actividades.add(actividad);
    }

    /**
     * @param titulo título de la actividad
     * @return la actividad con el título dado
     * @brief Buscar una actividad por su título
     */
    public Actividad buscarActividadPorTitulo(String titulo) {
        for (Actividad actividad : actividades) {
            if (actividad.getTitulo().equals(titulo)) {
                return actividad;
            }
        }
        throw new NoHayActividades();
    }

    /**
     * @return lista de todas las actividades abiertas
     * @throws NoHayActividades si no hay actividades abiertas
     * @brief Devuelve una lista con todas las actividades a las que es posible inscribirse
     */
    public List<Actividad> buscaTodasActividadesAbiertas() {
        List<Actividad> actividadesAbiertas = new LinkedList<>();
        for (Actividad actividad : actividades) {
            if (actividad.isAbierta()) {
                actividadesAbiertas.add(actividad);
            }
        }
        if (actividadesAbiertas.isEmpty()) {
            throw new NoHayActividades();
        }
        return actividadesAbiertas;
    }

}
