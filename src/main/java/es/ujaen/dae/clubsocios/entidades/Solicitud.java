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
     * @param solicitante Socio que realiza la solicitud
     */
    public Solicitud(int nAcompanantes, LocalDate fecha, Socio solicitante) {
        this.nAcompanantes = nAcompanantes;
        this.fecha = fecha;
        this.solicitante = solicitante;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

}