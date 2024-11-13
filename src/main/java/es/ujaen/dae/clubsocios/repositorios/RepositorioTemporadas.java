package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.excepciones.TemporadaYaExistente;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RepositorioTemporadas {

    private Map<Integer, Temporada> temporadas;

    public RepositorioTemporadas() {
        temporadas = new HashMap<>();
    }

    /**
     * @brief Crea una nueva temporada
     */
    public void crearTemporada() {
        Temporada temporada = new Temporada();
        if (temporadas.containsKey(temporada.getAnio()))
            throw new TemporadaYaExistente();

        temporadas.put(temporada.getAnio(), temporada);
    }

    /**
     * @param anio año de la temporada
     * @return temporada con el año dado
     * @brief Busca una temporada por su año
     */
    public Optional<Temporada> buscarPorAnio(int anio) {
        return Optional.ofNullable(temporadas.get(anio));
    }

    /**
     * @return lista de todas las temporadas
     * @brief Busca todas las temporadas
     */
    public List<Temporada> buscarTodasTemporadas() {
        return temporadas.values().stream().collect(Collectors.toList());
    }
}
