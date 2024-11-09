package es.ujaen.dae.clubsocios.objetosValor;


import es.ujaen.dae.clubsocios.entidades.Socio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public class Solicitud {

    @PositiveOrZero
    private int id;
    @Min(0)
    @Max(5)
    private int nAcompanantes;
    private LocalDate fecha;
    @PositiveOrZero
    private int plazasAceptadas;
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "El email no es válido")
    private String emailSocio;
    @PositiveOrZero
    private int idActividad;

    /**
     * @brief Constructor por defecto de la clase solicitud
     */
    public Solicitud() {
        this.id = 0;
        this.nAcompanantes = 0;
        this.fecha = LocalDate.now();
        this.plazasAceptadas = 0;
        this.emailSocio = "";
        this.idActividad = 0;
    }

    /**
     * @param nAcompanantes numero de acompañantes
     * @param emailSocio    Socio que realiza la solicitud
     * @brief Constructor parametrizado
     */
    public Solicitud(String emailSocio, int idActividad, int nAcompanantes) {
        this.id = 0;
        this.nAcompanantes = nAcompanantes;
        this.fecha = LocalDate.now();
        this.emailSocio = emailSocio;
        this.plazasAceptadas = 0;
        this.idActividad = idActividad;
    }

    /**
     * @param nAcompanantes número entero de acompañantes
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public void modificarAcompanantes(@PositiveOrZero int nAcompanantes) {
        this.nAcompanantes = nAcompanantes;
    }

    public String getEmailSocio() {
        return emailSocio;
    }

    @Min(0)
    @Max(5)
    public int getnAcompanantes() {
        return nAcompanantes;
    }

    public void setPlazasAceptadas(@PositiveOrZero int plazasAceptadas) {
        this.plazasAceptadas = plazasAceptadas;
    }

    @PositiveOrZero
    public int getPlazasAceptadas() {
        return plazasAceptadas;
    }

    @PositiveOrZero
    public int getId() {
        return id;
    }

    public void setId(@PositiveOrZero int id) {
        this.id = id;
    }

    public void aceptarPlaza() {
        if (this.plazasAceptadas < this.nAcompanantes) {
            this.plazasAceptadas++;
        }
    }

    public void quitarPlaza() {
        if (this.plazasAceptadas > 0) {
            this.plazasAceptadas--;
        }
    }
}