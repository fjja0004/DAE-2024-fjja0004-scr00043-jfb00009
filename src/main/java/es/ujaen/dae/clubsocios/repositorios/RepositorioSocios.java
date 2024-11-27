package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioSocios {
    @PersistenceContext
    EntityManager em;

    public Optional<Socio> buscar(String email) {
        return Optional.ofNullable(em.find(Socio.class, email));
    }

    public void guardar(Socio socio) {
        if (buscar(socio.getEmail()).isPresent())
            throw new SocioYaRegistrado();
        em.persist(socio);
    }

    /**
     * @return lista de todos los socios
     * @brief Busca todos los socios
     */
    public List<Socio> buscarTodos() {
        return em.createQuery("SELECT s FROM Socio s", Socio.class).getResultList();
    }

    /**
     * @brief Marca todas las cuotas como no pagadas
     */
    public void marcarTodasCuotasNoPagadas() {
        List<Socio> socios = buscarTodos();
        for (Socio socio : socios) {
            socio.setCuotaPagada(false);
        }
    }

    public void marcarCuotaPagada(Socio socio) {
        socio = em.find(socio.getClass(), socio.getEmail());
        socio.setCuotaPagada(true);
    }
}
