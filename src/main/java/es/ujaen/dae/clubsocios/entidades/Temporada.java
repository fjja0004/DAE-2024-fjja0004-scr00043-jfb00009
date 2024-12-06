package es.ujaen.dae.clubsocios.entidades;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Temporada {
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
     * @param actividad actividad a añadir
     * @brief Añade una nueva actividad a la temporada
     */
    public void nuevaActividad(Actividad actividad) {
        actividad.fechasValidas();
        actividades.add(actividad);
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public int getAnio() {
        return anio;
    }
}
