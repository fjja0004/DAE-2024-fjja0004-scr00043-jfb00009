package es.ujaen.dae.clubsocios.entidades;


import jakarta.validation.constraints.*;

import java.time.LocalDate;


public class Solicitud {

    @Min(0) @Max(5)
    private int nAcompanantes;

    private LocalDate fecha;
    @PositiveOrZero
    private int insAceptadas;

    Socio solicitante;

    /**
     * @brief Constructor por defecto de la clase solicitud
     */
    public Solicitud(Socio socio) {
        this.nAcompanantes = 0;
        this.fecha = LocalDate.now();
        this.insAceptadas = 0;
        this.solicitante = socio;
    }

    /**
     * @brief Constructor parametrizado
     * @param nAcompanantes numero de acompañantes
     * @param fecha fecha en la que se realiza la solicitud
     * @param insAceptadas numero de inscripciones aceptadas
     * @param solicitante Socio que realiza la solicitud
     */
    public Solicitud(int nAcompanantes, LocalDate fecha, int insAceptadas, Socio solicitante) {
        this.nAcompanantes = nAcompanantes;
        this.fecha = fecha;
        this.insAceptadas = insAceptadas;
        this.solicitante = solicitante;
    }

    // getters y setters de los diferentes atributos
    public int getnAcompanantes() {
        return nAcompanantes;
    }

    public void setnAcompanantes(int nAcompanantes) {
        this.nAcompanantes = nAcompanantes;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getInsAceptadas() {
        return insAceptadas;
    }

    public void setInsAceptadas(int insAceptadas) {
        this.insAceptadas = insAceptadas;
    }

    public Socio getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Socio solicitante) {
        this.solicitante = solicitante;
    }
}