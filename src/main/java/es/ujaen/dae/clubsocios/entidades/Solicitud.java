package es.ujaen.dae.clubsocios.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Entity
public class Solicitud {
    @Id
    @PositiveOrZero
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Min(0)
    @Max(5)
    private int nAcompanantes;
    private LocalDate fecha;
    @PositiveOrZero
    private int plazasAceptadas;
    @ManyToOne
    Socio socio;

    /**
     * @brief Constructor por defecto de la clase solicitud
     */
    public Solicitud() {
        this.nAcompanantes = 0;
        this.fecha = LocalDate.now();
        this.plazasAceptadas = 0;
        this.socio = null;
    }

    public Solicitud(int id, Socio socio, int nAcompanantes, LocalDate fecha, int plazasAceptadas) {
        this.id = id;
        this.nAcompanantes = nAcompanantes;
        this.fecha = fecha;
        this.socio = socio;
        this.plazasAceptadas = plazasAceptadas;
    }

    /**
     * @param nAcompanantes numero de acompañantes
     * @param socio         Socio que realiza la solicitud
     * @brief Constructor parametrizado
     */
    public Solicitud(Socio socio, int nAcompanantes) {
        this(0, socio, nAcompanantes, LocalDate.now(), 0);
    }

    /**
     * @param nAcompanantes número entero de acompañantes
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public void modificarAcompanantes(@PositiveOrZero int nAcompanantes) {
        this.nAcompanantes = nAcompanantes;
    }

    public Socio getSocio() {
        return socio;
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

    public void aceptarPlaza() {
        if (this.plazasAceptadas <= this.nAcompanantes) {
            this.plazasAceptadas++;
        }
    }

    public void quitarPlaza() {
        if (this.plazasAceptadas > 0) {
            this.plazasAceptadas--;
        }
    }
}