package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.excepciones.ActividadYaExistente;
import es.ujaen.dae.clubsocios.excepciones.FechaNoValida;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
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

    public Actividad crearActividad(Actividad actividad) {
        if (actividad.getFechaCelebracion().isBefore(actividad.getFechaFinInscripcion())
                || actividad.getFechaFinInscripcion().isBefore(actividad.getFechaInicioInscripcion())
                || LocalDate.now().isAfter(actividad.getFechaInicioInscripcion())) {
            throw new FechaNoValida();

        }
        if (buscarPorId(actividad.getId()).isPresent()) {
            throw new ActividadYaExistente();
        } else {
            em.persist(actividad);
            return actividad;
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Actividad> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Actividad.class, id));
    }

    @Transactional
    public Actividad actualizar(Actividad actividad) {
        return em.merge(actividad);
    }

    public void comprobarErrores() {
        em.flush();
    }

    @Transactional
    public void guardarSolicitud(@Valid Socio socio, int nAcompanantes, Actividad actividad) {
        actividad = em.find(actividad.getClass(), actividad.getId());
        if (actividad.buscarSolicitudPorEmail(socio.getEmail()).isPresent()) {
            throw new SolicitudYaRealizada();
        }
        Solicitud solicitud = actividad.realizarSolicitud(socio, nAcompanantes);
        em.persist(solicitud);
    }

    public void borrarReserva(Solicitud solicitud) {
        em.remove(solicitud);
    }

}
