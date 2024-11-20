package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioSocios {
    @PersistenceContext
    EntityManager em;
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Socio> buscar(String email){

        return Optional.ofNullable(em.find(Socio.class,email));
    }
    @Transactional//(propagation = Propagation.SUPPORTS)
    public void guardar(Socio socio){

        if (buscar(socio.getEmail()).isPresent())
            throw new SocioYaRegistrado();
    em.persist(socio);
    }

    /**
     * @return lista de todos los socios
     * @brief Busca todos los socios
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Socio> buscarTodos() {
        return em.createQuery("SELECT s FROM Socio s", Socio.class).getResultList();
    }
    /**
     * @brief Marca todas las cuotas como no pagadas
     */

    @Transactional
    public void marcarTodasCuotasNoPagadas() {
        Query query = em.createQuery("UPDATE Socio s SET s.cuotaPagada = false");
        query.executeUpdate();
    }

    @Transactional
    public void marcarCuotasPagadaEnSocio(Socio socio) {
        socio = em.find(socio.getClass(), socio.getEmail());
        socio.setCuotaPagada(true);
    }
}
