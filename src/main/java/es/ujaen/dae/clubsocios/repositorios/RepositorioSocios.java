package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RepositorioSocios {

    private Map<String, Socio> socios;

    /**
     * @brief Constructor por defecto de la clase RepositorioSocios
     */
    public RepositorioSocios() {
        socios = new HashMap<>();
    }

    /**
     * @param socio socio a crear
     * @brief Crea un nuevo socio
     */
    public void crear(Socio socio) {
        if (socios.containsKey(socio.getEmail()))
            throw new SocioYaRegistrado();

        socios.put(socio.getEmail(), socio);
    }

    /**
     * @param email email del socio
     * @return socio con el email dado
     * @brief Busca un socio por su email
     */
    public Socio buscarPorEmail(String email) {
        if (!socios.containsKey(email))
            throw new SocioNoValido();
        return socios.get(email);
    }

    /**
     * @return lista de todos los socios
     * @brief Busca todos los socios
     */
    public List<Socio> buscaTodos() {
        return socios.values().stream().collect(Collectors.toList());
    }

    /**
     * @brief Marca todas las cuotas como no pagadas
     */
    public void marcarTodasCuotasNoPagadas() {
        socios.values().forEach(socio -> socio.setCuotaPagada(false));
    }
}
