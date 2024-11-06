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
    //private int contadorIds = 1;

//    private int generarId() {
//        return contadorIds++;
//    }

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

        //socio.setId(generarId());
        socios.put(socio.getEmail(), socio);
    }

    /**
     * @param id id del socio
     * @return socio con el id dado
     * @brief Busca un socio por su id
     */
//    public Socio buscarPorId(int id) {
//        if (!socios.containsKey(id))
//            throw new SocioNoValido();
//        return socios.get(id);
//    }

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
}
