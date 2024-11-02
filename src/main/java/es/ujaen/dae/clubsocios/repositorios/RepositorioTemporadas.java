package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.excepciones.TemporadaNoExistente;
import es.ujaen.dae.clubsocios.excepciones.TemporadaYaExistente;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Repository
public class RepositorioTemporadas {

    private Map<Integer, Temporada> temporadas;

    public RepositorioTemporadas() {
        temporadas= new HashMap<>();
    }
    /**
     * @brief constructor parametrizado
     * @param temporadas
     */
    public RepositorioTemporadas(Map<Integer, Temporada> temporadas) {
        this.temporadas = temporadas;
    }
    /**
     * @param temporada temporada a crear
     * @brief Crea una nueva temporada
     */
    public void crear(Temporada temporada) {
        if (temporadas.containsKey(temporada.getAnio()))
            throw new TemporadaYaExistente();

        temporadas.put(temporada.getAnio(), temporada);
    }



    /**
     * @param anio año de la temporada
     * @return temporada con el año dado
     * @brief Busca una temporada por su año
     */
    public Temporada buscarPorAnio(int anio) {
        if (!temporadas.containsKey(anio))
            throw new TemporadaNoExistente();
        return temporadas.get(anio);
    }


    /**
     * @return lista de todas las temporadas
     * @brief Busca todas las temporadas
     */
    public List<Temporada> buscaTodos() {
        return temporadas.values().stream().collect(Collectors.toList())
    }
}
