package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoDisponibilidadPlazas;
import es.ujaen.dae.clubsocios.excepciones.SolicitudNoValida;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private LocalDate fechaCelebracion;
    @NotNull
    private LocalDate fechaInicioInscripcion;
    @NotNull
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
        this.solicitudes = new HashMap<>();
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

    /**
     * @brief borrar la solicitud de la actividad
     * @param solicitudEmail Solicitud para inscripción a una actividad
     * @throws SolicitudNoValida en caso de que la solicitud no exista o se pase una solicitud invalida
     */
    public void borrarSolicitud( String solicitudEmail){
        if (solicitudes.containsKey(solicitudEmail)) {
            solicitudes.remove(solicitudEmail);
        }else{
            throw new SolicitudNoValida();
        }
    }


    /**
     * @return true si es posible realizar una solicitud, false en caso contrario
     * @brief Comprueba si es posible realizar una solicitud
     */
    public boolean isAbierta() {
        if (solicitudes.size() >= plazas)
            return false;
        if (LocalDate.now().isBefore(fechaInicioInscripcion) || LocalDate.now().isAfter(fechaFinInscripcion))
            return false;
        return true;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public LocalDate getFechaCelebracion() {
        return fechaCelebracion;
    }

    public LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }
}
