package es.ujaen.dae.clubsocios.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Temporada {
    @Positive
    @Id
    private int anio;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn
    List<Actividad> actividades;

    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        this.anio = LocalDate.now().getYear();
    }

    /**
     * @brief Añade una nueva actividad a la temporada
     * @param actividad actividad a añadir
     */
    public void nuevaActividad(Actividad actividad) {
        actividad.fechasValidas();
        actividades.add(actividad);
    }

    public int getAnio() {
        return anio;
    }
}
