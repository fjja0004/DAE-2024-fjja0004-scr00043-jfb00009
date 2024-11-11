package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.objetosValor.Actividad;
import es.ujaen.dae.clubsocios.excepciones.ActividadYaExistente;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
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
        Optional<Actividad> actividadExistente = buscarActividadPorTitulo(actividad.getTitulo());
        if ((!actividadExistente.isEmpty()) && actividadExistente.get().getFechaCelebracion() == actividad.getFechaCelebracion()) {
            throw new ActividadYaExistente();
        }

        actividad.fechasValidas();
        actividad.setId(generarId());
        actividades.add(actividad);
    }

    /**
     * @param titulo título de la actividad
     * @return la actividad con el título dado
     * @throws NoHayActividades lanza una excepción si no existe ninguna actividad.
     * @brief Buscar una actividad por su título
     */
    public Optional<Actividad> buscarActividadPorTitulo(String titulo) {
        if (actividades.isEmpty()) {
            return Optional.empty();
        }
        for (Actividad actividad : actividades) {
            if (actividad.getTitulo().equals(titulo)) {
                return Optional.of(actividad);
            }
        }
        return Optional.empty();
    }

    /**
     * @return lista de todas las actividades de la temporada actual
     * @throws NoHayActividades si no hay actividades en la temporada actual
     * @brief Devuelve una lista con todas las actividades de la temporada actual
     */
    public List<Actividad> buscarTodasTemporadaActual() {
        List<Actividad> actividadesTemporadaActual = new LinkedList<>();
        for (Actividad actividad : actividades) {
            if (actividad.getIdTemporada() == LocalDate.now().getYear()) {
                actividadesTemporadaActual.add(actividad);
            }
        }
        return actividadesTemporadaActual;
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
        return actividadesAbiertas;
    }

    /**
     * @param id id de la actividad
     * @return la actividad con el id dado
     * @brief Busca una actividad por su id
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
