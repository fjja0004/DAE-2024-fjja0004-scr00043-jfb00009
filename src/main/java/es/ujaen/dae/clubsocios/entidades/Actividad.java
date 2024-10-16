package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.excepciones.SolicitudNoValida;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import es.ujaen.dae.clubsocios.objetosValor.Solicitud;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    private List<Solicitud> solicitudesPendientes;
    private HashMap<String, Solicitud> solicitudesAceptadas;

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
        this.solicitudesPendientes = new LinkedList<>();
        this.solicitudesAceptadas = new HashMap<>();
    }

    /**
     * @param titulo String título de la actividad
     * @param descripcion String con una breve descripción de la actividad
     * @param precio Número entero de precio que cuesta la actividad
     * @param plazas Número entero de plazas que tiene una actividad
     * @param fechaCelebracion Fecha en la que se va a realizar la actividad
     * @param fechaInicioInscripcion Fecha en la que inicio el periodo de inscripción
     * @param fechaFinInscripcion Fecha en la que termina el periodo de inscripción
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
        this.solicitudesPendientes = new LinkedList<>();
        this.solicitudesAceptadas = new HashMap<>();
    }


    /**
     * @brief realiza una solicitud a la actividad
     * @param solicitud Solicitud
     */
    public void realizarSolicitud(@Valid Solicitud solicitud) {

        if (!(solicitud.getFecha().isAfter(fechaInicioInscripcion) && solicitud.getFecha().isBefore(fechaFinInscripcion))){
            if (solicitudesAceptadas.containsKey(solicitud.getSolicitante().getEmail())|| solicitudesPendientes.contains(solicitud))
                throw new SolicitudYaRealizada();

            if (solicitudesAceptadas.size()<plazas && solicitud.getSolicitante().isCuotaPagada()){
            solicitudesAceptadas.put(solicitud.getSolicitante().getEmail(),solicitud);
                }
            if (solicitudesAceptadas.size() >= plazas)
                solicitudesPendientes.add(solicitud);
            if (!solicitud.getSolicitante().isCuotaPagada()){
                solicitudesPendientes.add(solicitud);
            }

        } else{
            throw new SolicitudNoValida();
        }
    }

    /**
     * @brief Devuelve una lista con las solicitudes pendientes
     */
    public List<Solicitud> listaPendientes(){
        if (fechaFinInscripcion.isBefore(LocalDate.now())) {
            return solicitudesPendientes;
        }
        throw new InscripcionAbierta();
    }

    /**
     * @param solicitante socio que realiza la solicitud
     * @param acompanantes número de acompañantes que se aceptan
     * @brief Acepta una solicitud e indica el número de acompañantes
     */
    public void aceptarSolicitud(String solicitante, int acompanantes) {
        buscarSolicitud(solicitante).aceptarSolicitud(acompanantes);
    }

    public Solicitud buscarSolicitud(String solicitante) {
        for (Solicitud solicitud: solicitudesPendientes) {
            if (solicitud.getSolicitante().getEmail() == solicitante)
                return solicitud;
        }
        throw new SocioNoRegistrado();
    }

    /**
     * @param solicitudEmail Solicitud para inscripción a una actividad
     * @throws SolicitudNoValida en caso de que la solicitud no exista o se pase una solicitud inválida
     * @brief borrar la solicitud de la actividad
     */
    public void borrarSolicitud(String solicitudEmail) {
        if (solicitudesAceptadas.containsKey(solicitudEmail)) {
            solicitudesAceptadas.remove(solicitudEmail);
        }else{
            for(Solicitud sol: solicitudesPendientes){
                if(solicitudEmail == sol.getSolicitante().getEmail()){
                    solicitudesPendientes.remove(sol);
                    break;
                }
            }
        }
        throw new SolicitudNoValida();
    }

    /**
     * @return true si es posible realizar una solicitud, false en caso contrario
     * @brief Comprueba si es posible realizar una solicitud
     */
    public boolean isAbierta() {
        if (solicitudesAceptadas.size() >= plazas)
            return false;
        if (LocalDate.now().isBefore(fechaInicioInscripcion) || LocalDate.now().isAfter(fechaFinInscripcion))
            return false;
        return true;
    }

    /**
     * @param email         email del socio que realiza la solicitud
     * @param nAcompanantes número de acompañantes
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public void modificarAcompanantes(String email,int nAcompanantes) {
        if (solicitudesAceptadas.containsKey(email)) {
            solicitudesAceptadas.get(email).modificarAcompanantes(nAcompanantes);
        } else {
            throw new SolicitudNoValida();
        }
    }

    /**
     *
     * @return título de la actividad
     */
    public String getTitulo() {
        return titulo;
    }

    public void setPlazas(int  plazas) {
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
