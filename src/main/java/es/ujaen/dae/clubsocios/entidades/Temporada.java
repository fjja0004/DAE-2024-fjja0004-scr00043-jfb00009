package es.ujaen.dae.clubsocios.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
@Entity
public class Temporada {
    @Positive
    @Id
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
