package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;

import java.time.LocalDate;
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

    /**
     * @brief Devuelve una lista con todas las actividades de la temporada actual
     * @return lista de todas las actividades de la temporada actual
     * @throws NoHayActividades si no hay actividades en la temporada actual
     */
    public List<Actividad> buscarTodasTemporadaActual() {
        List<Actividad> actividadesTemporadaActual = new LinkedList<>();
        for (Actividad actividad : actividades) {
            if (actividad.getIdTemporada() == LocalDate.now().getYear()) {
                actividadesTemporadaActual.add(actividad);
            }
        }

        if (actividadesTemporadaActual.isEmpty()) {
            throw new NoHayActividades();
        }

        return actividadesTemporadaActual;
    }

    /**
     * @brief Devuelve una lista con todas las actividades a las que es posible inscribirse
     * @return lista de todas las actividades abiertas
     * @throws NoHayActividades si no hay actividades abiertas
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

    /**
     * @brief Busca una actividad por su id
     * @param id id de la actividad
     * @return la actividad con el id dado
     */
    public Actividad buscarPorId(int id) {
        for (Actividad actividad : actividades) {
            if (actividad.getId() == id) {
                return actividad;
            }
        }
        throw new NoHayActividades();
    }
}
