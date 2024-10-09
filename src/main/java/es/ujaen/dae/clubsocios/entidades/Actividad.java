package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoDisponibilidadPlazas;
import es.ujaen.dae.clubsocios.excepciones.SolicitudNoValida;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.HashMap;

public class Actividad {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @Positive
    private int precio;
    @Positive
    private int plazas;

    private LocalDate fechaCelebracion;

    private LocalDate fechaInicioInscripcion;

    private LocalDate fechaFinInscripcion;

    private HashMap<String, Solicitud> solicitudes;

    /**
     * @brief Constructor por defecto de la clase Actividad
     */
    public Actividad() {
        this.titulo = "";
        this.descripcion = "";
        this.precio = 0;
        this.plazas = 0;
        this.fechaCelebracion = LocalDate.now();
        this.fechaInicioInscripcion = LocalDate.now();
        this.fechaFinInscripcion = LocalDate.now();
        this.solicitudes = new HashMap<>();
    }

    /**
     * @param titulo
     * @param descripcion
     * @param precio
     * @param plazas
     * @param fechaCelebracion
     * @param fechaInicioInscripcion
     * @param fechaFinInscripcion
     * @brief Constructor parametrizado de la clase Actividad
     */
    public Actividad(String titulo, String descripcion, int precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
    }

    public void realizarSolicitud(@Valid Solicitud solicitud) {

        if (solicitudes.containsKey(solicitud.getSolicitante().getEmail()))
            throw new SolicitudYaRealizada();

        if (!(solicitud.getFecha().isAfter(fechaInicioInscripcion) && solicitud.getFecha().isBefore(fechaFinInscripcion)))
            throw new SolicitudNoValida();

        if (solicitudes.size() >= plazas)
            throw new NoDisponibilidadPlazas();

        solicitudes.put(solicitud.getSolicitante().getEmail(), solicitud);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

}
