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
import java.util.LinkedList;
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
    private int plazasOcupadas; //Plazas ocupadas por socios con cuota pagada
    @NotNull
    private LocalDate fechaInicioInscripcion;
    @NotNull
    private LocalDate fechaFinInscripcion;
    @NotNull
    private LocalDate fechaCelebracion;
    @OneToMany
    @JoinColumn(name = "actividad")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Actividad actividad = (Actividad) o;
        return getId() == actividad.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * @param email email del solicitante
     * @return solicitud de inscripción a la actividad
     * @brief Comprueba si existe una solicitud de inscripción a la actividad
     */
    public Optional<Solicitud> buscarSolicitudPorEmail(String email) {

        if (solicitudes.isEmpty()) {
            return Optional.empty();
        }
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getSocio().getEmail().equals(email)) {
                return Optional.of(solicitud);
            }
        }
        return Optional.empty();
    }

    /**
     * @param solicitud solicitud a añadir
     * @brief Crea una solicitud de inscripción a la actividad
     */
    public void crearSolicitud(Solicitud solicitud) {
        //Comprobamos que la actividad esté abierta.
        if (this.isAbierta()) {

            //Comprobamos que no haya una solicitud del mismo socio.
            if (buscarSolicitudPorEmail(solicitud.getSocio().getEmail()).isPresent()) {
                throw new SolicitudYaRealizada();
            }

            //Aumentamos las plazas ocupadas si quedan plazas y el socio tiene la cuota pagada.
            if (plazasOcupadas < plazas && solicitud.socio.isCuotaPagada()) {
                //Si el socio tiene la cuota pagada, se le asigna una plaza.
                solicitud.setPlazasAceptadas(1);
                plazasOcupadas++;
            }
            //Añadimos la solicitud a la lista de solicitudes, aunque no tenga la cuota pagada.
            solicitudes.add(solicitud);

        } else {
            throw new InscripcionCerrada();
        }
    }

    /**
     * @param solicitud solicitud que se quiere cancelar
     * @throws SolicitudNoExistente en caso de que la solicitud no exista
     * @throws InscripcionCerrada   en caso de que el período de inscripción esté cerrado
     * @brief Cancela una solicitud de inscripción a una actividad
     */
    public void cancelarSolicitud(Solicitud solicitud) {
        if (!this.isAbierta())
            throw new InscripcionCerrada();

        if (solicitud.getSocio() == null)
            throw new SolicitudNoExistente();

        for (Solicitud sol : solicitudes) {
            if (sol.getId() == solicitud.getId()) {
                if (sol.getPlazasAceptadas() == 1) {
                    plazasOcupadas--;
                }
                solicitudes.remove(sol);
                break;
            }
        }
    }

    /**
     * @param solicitud     solicitud a modificar
     * @param nAcompanantes número de acompañantes
     * @throws SolicitudNoExistente en caso de que la solicitud no exista
     * @brief modifica el número de acompañantes que tendrá una solicitud
     */
    public Solicitud modificarAcompanantes(Solicitud solicitud, int nAcompanantes) {
        if (!this.isAbierta())
            throw new InscripcionCerrada();

        for (Solicitud sol : solicitudes) {
            if (sol.getId() == solicitud.getId()) {
                sol.modificarAcompanantes(nAcompanantes);
                return sol;
            }
        }
        throw new SolicitudNoExistente();
    }

    /**
     * @param solicitud solicitud a la que se le acepta la plaza
     * @brief Acepta una plaza de una solicitud de inscripción a la actividad
     */
    public Solicitud aceptarPlaza(Solicitud solicitud) {
        if (isAbierta()) throw new InscripcionAbierta();

        if (plazasOcupadas == plazas)
            throw new NoDisponibilidadPlazas();

        for (Solicitud sol : solicitudes) {
            if ((sol.getId() == solicitud.getId())) {
                sol.aceptarPlaza();
                plazasOcupadas++;
                return sol;
            }
        }
        throw new SolicitudNoExistente();
    }

    /**
     * @param solicitud solicitud a la que se le acepta la plaza
     * @brief Retira una plaza de una solicitud de inscripción a la actividad
     */
    public Solicitud quitarPlaza(Solicitud solicitud) {
        if (isAbierta()) throw new InscripcionAbierta();

        for (Solicitud sol : solicitudes) {
            if ((sol.getId() == solicitud.getId())) {
                sol.quitarPlaza();
                if (plazasOcupadas > 0)
                    plazasOcupadas--;
                return sol;
            }
        }
        throw new SolicitudNoExistente();
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
        if (fechaCelebracion.isBefore(fechaInicioInscripcion) || fechaCelebracion.isBefore(fechaFinInscripcion) || fechaInicioInscripcion.isAfter(fechaFinInscripcion))
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
        List<Solicitud> listaSolicitudes = new LinkedList<>();
        for (Solicitud solicitud : solicitudes) {
            listaSolicitudes.add(solicitud);
        }
        return listaSolicitudes;
    }

    @PositiveOrZero
    public int getPlazasOcupadas() {
        return plazasOcupadas;
    }

    public @NotNull LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public @NotNull LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

}
