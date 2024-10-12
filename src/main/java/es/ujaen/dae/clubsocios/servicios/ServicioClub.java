package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.objetosValor.Solicitud;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;

@Service
@Repository
@Validated
public class ServicioClub {
    private final Map<String, Socio> socios;
    private final ArrayList<Temporada> temporadas;

    // Socio especial que representa al administrador del club
    private static final Socio admin = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");

    /**
     * @brief constructor por defecto de la clase ServicioClub
     */
    public ServicioClub() {
        socios = new HashMap<>();
        temporadas = new ArrayList<>();
        temporadas.add(new Temporada(LocalDate.now().getYear()));
    }

    public Optional<Socio> login(@Email String email, String clave) {

        if (admin.getEmail().equals(email) && admin.getClave().equals(clave))
            return Optional.of(admin);

        Socio socio = socios.get(email);
        return (socio != null && socio.getClave().equals(clave)) ? Optional.of(socio) : Optional.empty();

    }

    public void anadirSocio(@Valid Socio socio) {
        // Evitar que se cree un usuario con la cuenta de administrador
        if (socio.getEmail().equals(admin.getEmail()))
            throw new SocioYaRegistrado();

        if (socios.containsKey(socio.getEmail()))
            throw new SocioYaRegistrado();

        socios.put(socio.getEmail(), socio);

    }

    void crearActividad(@Valid Actividad a) {

        Temporada temporadaActual = temporadas.getLast();

        if (a.getFechaInicioInscripcion().isAfter(a.getFechaFinInscripcion()) || a.getFechaInicioInscripcion().isAfter(a.getFechaCelebracion()) || a.getFechaFinInscripcion().isAfter(a.getFechaCelebracion()))
            throw new FechaNoValida();

        if (temporadaActual.buscarActividadPorTitulo(a.getTitulo()) != null)
            throw new ActividadYaExistente();

        temporadas.getLast().crearActividad(a);
    }

    void revisarSolicitudes() {


    }

    /**
     * @param socio Socio que paga la cuota
     * @brief marca la cuota del socio como pagada, en caso de que ya esté pagado lanza una excepción
     */
    void marcarCuotaPagada(@Valid Socio socio) {

        if (!socios.get(socio).isCuotaPagada()) {
            socios.get(socio).setCuotaPagada(true);
        } else {
            throw new PagoYaRealizado();
        }
    }

    /**
     * @param titulo String título de la actividad
     * @return Actividad encontrada con el título correspondiente en la temporada actual
     * @throws NoHayActividades en caso de que no exista actividad con ese título correspondiente
     * @brief busca una actividad por título en el año actual
     */
    Actividad buscarActividad(@NotBlank String titulo) {
        if (temporadas.getLast().buscarActividadPorTitulo(titulo) != null)
            return temporadas.getLast().buscarActividadPorTitulo(titulo);
        else throw new NoHayActividades();
    }

    /**
     * @param nAcompanantes número entero de acompañantes
     * @param actividad     Actividad para la que se realiza la solicitud
     * @param socio         Socio que va a realizar la solicitud
     * @brief realiza la solicitud de una actividad
     */
    void realizarSolicitud(int nAcompanantes, Actividad actividad, Socio socio) {
        if (temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()) == null) {
            throw new NoHayActividades();
        } else {
            LocalDate fechaActual = LocalDate.now();
            Solicitud nuevaSolicitud = new Solicitud(nAcompanantes, fechaActual, socio);
            temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).realizarSolicitud(nuevaSolicitud);
        }
    }

    /**
     * @param socio         Socio que va a realizar la modificación
     * @param actividad     Actividad a la que se va a modificar el número de acompañantes
     * @param nAcompanantes número entero de acompañantes
     * @brief modifica el número de acompañantes que tendrá un socio
     */
    void modificarAcompanantes(Socio socio, Actividad actividad, int nAcompanantes) {
        temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).modificarAcompanantes(socio.getEmail(), nAcompanantes);
    }

    /**
     * @param actividad Actividad
     * @param socio     Socio solicitante
     * @brief borra las solicitudes que realiza un socio a una actividad
     */
    void borrarSolicitud(@Valid Actividad actividad, @Valid Socio socio) {
        temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).borrarSolicitud(socio.getEmail());
    }

    /**
     * @brief crea una nueva temporada al inicio de cada año
     */
    @Scheduled(cron = "0 0 0 1 1 ? *")
    void crearNuevaTemporada() {
        temporadas.add(new Temporada(LocalDate.now().getYear()));
    }
}
