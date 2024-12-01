package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubsocios.excepciones.TemporadaYaExistente;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class RepositorioTemporadas {

    @PersistenceContext
    EntityManager em;

    /**
     * @brief Crea una nueva temporada
     */
    public void crearTemporada() {
        Temporada temporada = new Temporada();
        if (!buscar(LocalDate.now().getYear()).isPresent()) {
            em.persist(temporada);
        }
    }

    public Optional<Temporada> buscar(int anio) {
        return Optional.ofNullable(em.find(Temporada.class, anio));
    }

    /**
     * @return lista de todas las temporadas
     * @brief Busca todas las temporadas
     */
    public List<Temporada> buscarTodasTemporadas() {
        return em.createQuery("SELECT t FROM Temporada t", Temporada.class).
                getResultList();
    }

    public Temporada temporadaActual() {
        return buscar(LocalDate.now().getYear()).get();
    }

    public Temporada actualizar(Temporada temporada) {
        return em.merge(temporada);
    }

    public void comprobarErrores() {
        em.flush();
    }

}
