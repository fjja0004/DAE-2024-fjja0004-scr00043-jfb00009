package es.ujaen.dae.clubsocios.entidades;

import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class Temporada {
    @Positive
    private int anio;

    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        this.anio = LocalDate.now().getYear();
    }

    public int getAnio() {
        return anio;
    }
}
