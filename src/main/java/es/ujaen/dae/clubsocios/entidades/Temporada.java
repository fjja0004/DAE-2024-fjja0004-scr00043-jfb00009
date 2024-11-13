package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.objetosValor.Actividad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Temporada {
    @Positive
    @Id
    private int anio;

    @ElementCollection
    @CollectionTable (name ="actividades")
    List<Actividad> actividades;
    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        this.anio = LocalDate.now().getYear();
        this.actividades = new ArrayList<>();
    }

    public int getAnio() {
        return anio;
    }
}
