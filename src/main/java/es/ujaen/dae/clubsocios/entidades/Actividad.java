package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.excepciones.SolicitudNoValida;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Entity
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @Positive
    private int precio;
    @Positive
    private int plazas;
    @PositiveOrZero
    private int plazasOcupadas;
    @NotNull
    private LocalDate fechaInicioInscripcion;
    @NotNull
    private LocalDate fechaFinInscripcion;
    @NotNull
    private LocalDate fechaCelebracion;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<Solicitud> solicitudes;

    /**
     * @brief Constructor por defecto de la clase Actividad
     */
    public Actividad() {
        this.titulo = "";
        this.descripcion = "";
        this.precio = 0;
        this.plazas = 0;
        this.fechaInicioInscripcion = LocalDate.now();
        this.fechaFinInscripcion = LocalDate.now();
        this.fechaCelebracion = LocalDate.now();
    }

    /**
     * @param titulo                 String título de la actividad
     * @param descripcion            String con una breve descripción de la actividad
     * @param precio                 Número entero de precio que cuesta la actividad
     * @param plazas                 Número entero de plazas que tiene una actividad
     * @param fechaInicioInscripcion Fecha en la que inicio el periodo de inscripción
     * @param fechaFinInscripcion    Fecha en la que termina el periodo de inscripción
     * @param fechaCelebracion       Fecha en la que se va a realizar la actividad
     * @brief Constructor parametrizado de la clase Actividad
     */
    public Actividad(String titulo, String descripcion, int precio, int plazas, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion, LocalDate fechaCelebracion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.fechaCelebracion = fechaCelebracion;
    }

    /**
     * @param email email del solicitante
     * @return solicitud de inscripción a la actividad
     * @brief Comprueba si existe una solicitud de inscripción a la actividad
     */

    //TODO revisar creo que hay que cambiarlo
    public Optional<Solicitud> buscarSolicitudPorEmail(String email) {

        if(solicitudes.isEmpty()){
            throw new SolicitudNoExistente();
        }
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getSocio().equals(email)) {
                return Optional.of(solicitud);
            }
        }
        return Optional.empty();
    }

    /**
     * @param socio         socio que realiza la solicitud
     * @param nAcompanantes número de acompañantes
     * @brief Realiza una solicitud de inscripción a una actividad
     */
    public Solicitud realizarSolicitud(@Valid Socio socio, int nAcompanantes) {

        if (this.isAbierta()) {
            Solicitud solicitud = new Solicitud(socio, nAcompanantes);
            if (plazasOcupadas < plazas && socio.isCuotaPagada()) {
                solicitud.setPlazasAceptadas(1);
                plazasOcupadas++;
            }
            solicitudes.add(solicitud);
            return solicitud;
        } else {
            throw new SolicitudNoValida();
        }

    }

    /**
     * @param email email del solicitante
     * @brief Cancela una solicitud de inscripción a una actividad
     */
    public void cancelarSolicitud(String email) {
        if (!isAbierta())
            throw new InscripcionCerrada();

        buscarSolicitudPorEmail(email).ifPresentOrElse(solicitud -> {
            if (solicitud.getPlazasAceptadas() == 1) {
                plazasOcupadas--;
            }
            solicitudes.remove(solicitud);
        }, () -> {
            throw new SolicitudNoExistente();
        });
    }

    /**
     * @param email         email del socio que realiza la solicitud
     * @param nAcompanantes número de acompañantes
     * @throws SolicitudNoValida en caso de que la solicitud no sea válida
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public void modificarAcompanantes(String email, int nAcompanantes) {
        if (!isAbierta())
            throw new InscripcionCerrada();
        buscarSolicitudPorEmail(email).ifPresentOrElse(solicitud -> solicitud.modificarAcompanantes(nAcompanantes), () -> {
            throw new SolicitudNoExistente();
        });
    }

    /**
     * @param email email del solicitante
     * @brief Acepta una plaza de una solicitud de inscripción a la actividad
     */
    public void aceptarPlaza(String email) {
        if (isAbierta())
            throw new InscripcionAbierta();
        if (plazas > plazasOcupadas) {
            buscarSolicitudPorEmail(email).ifPresentOrElse(solicitud -> {
                solicitud.aceptarPlaza();
                plazasOcupadas++;
            }, () -> {
                throw new SolicitudNoExistente();
            });
        }
    }

    /**
     * @param email email del solicitante
     * @brief Retira una plaza de una solicitud de inscripción a la actividad
     */
    public void quitarPlaza(String email) {
        if (isAbierta())
            throw new InscripcionAbierta();

        buscarSolicitudPorEmail(email).ifPresentOrElse(solicitud -> {
            solicitud.quitarPlaza();
            plazasOcupadas--;
        }, () -> {
            throw new SolicitudNoExistente();
        });

    }

    /**
     * @return true si es posible realizar una solicitud, false en caso contrario
     * @brief Comprueba si es posible realizar una solicitud
     */
    public boolean isAbierta() {
        if (LocalDate.now().isBefore(fechaInicioInscripcion) || LocalDate.now().isAfter(fechaFinInscripcion))
            return false;
        return true;
    }

    /**
     * @throws FechaNoValida en caso de que las fechas no sean válidas
     * @brief Comprueba si las fechas de la actividad son válidas
     */
    public void fechasValidas() {
        if (fechaCelebracion.isBefore(fechaInicioInscripcion) || fechaCelebracion.isBefore(fechaFinInscripcion)
                || fechaInicioInscripcion.isAfter(fechaFinInscripcion))
            throw new FechaNoValida();
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFechaCelebracion() {
        return fechaCelebracion;
    }

    public Actividad setFechaInicioInscripcion(@NotNull LocalDate fechaInicioInscripcion) {
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        return this;
    }

    public Actividad setFechaFinInscripcion(@NotNull LocalDate fechaFinInscripcion) {
        this.fechaFinInscripcion = fechaFinInscripcion;
        return this;
    }

    public Actividad setFechaCelebracion(@NotNull LocalDate fechaCelebracion) {
        this.fechaCelebracion = fechaCelebracion;
        return this;
    }

    public int getId() {
        return id;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    @PositiveOrZero
    public int getPlazasOcupadas() {
        return plazasOcupadas;
    }

    @Positive
    public int getPlazas() {
        return plazas;
    }
}
