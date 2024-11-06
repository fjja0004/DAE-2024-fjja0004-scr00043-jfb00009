package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import es.ujaen.dae.clubsocios.objetosValor.Actividad;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Temporada {
    @Positive
    private int anio;

    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        this.anio = LocalDate.now().getYear();
    }

    /**
     * @param anio año de la temporada
     * @param anio año de la temporada
     * @brief Constructor parametrizado de la clase Temporada
     */
    public Temporada(int anio) {
        this.anio = anio;
    }

    public int getAnio() {
        return anio;
    }
}
