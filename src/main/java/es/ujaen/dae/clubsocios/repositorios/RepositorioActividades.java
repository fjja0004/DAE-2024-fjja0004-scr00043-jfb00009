package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.excepciones.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class RepositorioActividades {

    @PersistenceContext
    EntityManager em;

    /**
     * @param actividad actividad a crear
     * @brief Crea una nueva actividad
     */
    public Actividad guardarActividad(Actividad actividad) {
        actividad.fechasValidas();
        em.persist(actividad);
        return actividad;
    }

    public List<Integer> listadoIds() {
        return em.createQuery("select h.id from Actividad h").getResultList();
    }

    /**
     * @return lista de todas las actividades abiertas
     * @throws NoHayActividades si no hay actividades abiertas
     * @brief Devuelve una lista con todas las actividades a las que es posible inscribirse
     */
    public List<Actividad> buscaTodasActividadesAbiertas() {
        return em.createQuery("SELECT a FROM Actividad a WHERE a.fechaInicioInscripcion <= :fechaActual AND a.fechaFinInscripcion>:fechaActual", Actividad.class)
                .setParameter("fechaActual", LocalDate.now()).getResultList();
    }

    /**
     * @param id id de la actividad
     * @return optional la actividad con el id dado
     * @brief Busca una actividad por su id
     */
    public Optional<Actividad> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Actividad.class, id));
    }

    public Actividad actualizar(Actividad actividad) {
        return em.merge(actividad);
    }

    public Solicitud actualizar(Solicitud solicitud) {
        return em.merge(solicitud);
    }

    public void comprobarErrores() {
        em.flush();
    }

    public void guardarSolicitud(Solicitud solicitud) {
        em.persist(solicitud);
    }

    public void borrarSolicitud(Solicitud solicitud) {
        em.remove(em.merge(solicitud));
    }

    public void modificarFechaActividad(Actividad actividad) {
        actividad.fechasValidas();
        Actividad actividadOrig = em.find(Actividad.class, actividad.getId());
        actividadOrig.setFechaFinInscripcion(actividad.getFechaFinInscripcion());
        actividadOrig.setFechaInicioInscripcion(actividad.getFechaInicioInscripcion());
        actividadOrig.setFechaCelebracion(actividad.getFechaCelebracion());
    }
}
