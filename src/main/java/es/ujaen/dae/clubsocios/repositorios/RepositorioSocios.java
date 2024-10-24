package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoRegistrado;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RepositorioSocios {

    private Map<Integer, Socio> socios;
    private int contadorIds = 1;

    private int generarId() {
        return contadorIds++;
    }

    /**
     * @brief Constructor por defecto de la clase RepositorioSocios
     */
    public RepositorioSocios() {
        socios = new HashMap<>();

        //Datos de prueba que se eliminanarán más adelante
        Socio socio1 = new Socio("Socio1", "-", "socio1@gmail.com", "123456789", "clave");
        Socio socio2 = new Socio("Socio2", "-", "socio1@gmail.com", "123456789", "clave");
        Socio socio3 = new Socio("Socio3", "-", "socio1@gmail.com", "123456789", "clave");

        socio1.setId(generarId());
        socio2.setId(generarId());
        socio3.setId(generarId());

        socios.put(socio1.getId(), socio1);
        socios.put(socio2.getId(), socio2);
        socios.put(socio3.getId(), socio3);
    }

    /**
     * @param socio socio a crear
     * @brief Crea un nuevo socio
     */
    public void crear(Socio socio) {
        if (socios.containsValue(socio))
            throw new SocioYaRegistrado();

        socio.setId(generarId());
        socios.put(socio.getId(), socio);
    }

    /**
     * @param id id del socio
     * @return socio con el id dado
     * @brief Busca un socio por su id
     */
    public Socio buscarPorId(int id) {
        if (!socios.containsKey(id))
            throw new SocioNoRegistrado();
        return socios.get(id);
    }

    /**
     * @param email email del socio
     * @return socio con el email dado
     * @brief Busca un socio por su email
     */
    public Socio buscarPorEmail(String email) {
        for (Socio socio : socios.values()) {
            if (socio.getEmail().equals(email))
                return socio;
        }
        throw new SocioNoRegistrado();
    }

}
