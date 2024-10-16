package es.ujaen.dae.clubsocios.objetosValor;


import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.DemasiadosAcompanantes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public class Solicitud {

    @Min(0)
    @Max(5)
    private int nAcompanantes;

    private LocalDate fecha;
    @PositiveOrZero
    private int acompanantesAceptados;
    private boolean aceptada;
    @Valid
    private Socio solicitante;

    /**
     * @brief Constructor por defecto de la clase solicitud
     */
    public Solicitud(Socio socio) {
        this.nAcompanantes = 0;
        this.fecha = LocalDate.now();
        this.acompanantesAceptados = 0;
        this.solicitante = socio;
        this.aceptada = false;
    }

    /**
     * @param nAcompanantes numero de acompañantes
     * @param solicitante   Socio que realiza la solicitud
     * @brief Constructor parametrizado
     */
    public Solicitud(int nAcompanantes, Socio solicitante) {
        this.nAcompanantes = nAcompanantes;
        this.fecha = LocalDate.now();
        this.solicitante = solicitante;
        this.acompanantesAceptados = 0;
        this.aceptada = false;
    }

    /**
     * @param nAcompanantes número entero de acompañantes
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public void modificarAcompanantes(@PositiveOrZero int nAcompanantes) {
        this.nAcompanantes = nAcompanantes;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Socio getSolicitante() {
        return solicitante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void aceptarSolicitud(int acompanantesAceptados) {
        this.aceptada = true;
        if (acompanantesAceptados <= this.acompanantesAceptados)
            this.acompanantesAceptados = acompanantesAceptados;
        else {
            throw new DemasiadosAcompanantes();
        }
    }
}