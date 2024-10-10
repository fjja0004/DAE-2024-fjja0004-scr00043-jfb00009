package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
     * @brief  constructor por defecto de la clase ServicioClub
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

    public void borrarSocio(@Valid Socio socio) {
        // Evitar que se borre el usuario con la cuenta de administrador
        if (socio.getEmail().equals(admin.getEmail()))
            throw new IntentoBorrarAdmin();

        if (socios.containsKey(socio.getEmail()))
            socios.remove(socio.getEmail());
        else {
            // Lanzar excepción si el socio no está registrado
            throw new SocioNoRegistrado();
        }

        //TODO sólo borrar el socio si no tiene solicitudes
    }

    void crearActividad(@Valid Actividad a) {

        Temporada temporadaActual = temporadas.getLast();

        if (a.getFechaInicioInscripcion().isAfter(a.getFechaFinInscripcion()) || a.getFechaInicioInscripcion().isAfter(a.getFechaCelebracion()) || a.getFechaFinInscripcion().isAfter(a.getFechaCelebracion()))
            throw new FechaNoValida();

        if (temporadaActual.buscarActividadPorTitulo(a.getTitulo())!=null)
            throw new ActividadYaExistente();

        temporadas.getLast().crearActividad(a);
    }

    void revisarSolicitudes() {

    }

    /**
     *
     * @brief marca la cuota del socio como pagada, en caso de que ya esté pagado lanza una excepción
     * @param socio Socio que paga la cuota
     */
    void marcarCuotaPagada(@Valid Socio socio) {

        if (!socios.get(socio).isCuotaPagada()) {
            socios.get(socio).setCuotaPagada(true);
        } else {
            throw new PagoYaRealizado();
        }
    }

    /*
     * Actividad buscarActividad(@NotBlank String titulo,@Positive @PositiveOrZero int anio) {
     * for (Temporada elemento : temporada) {
     * if (elemento.getAnio()==anio) {
     * return elemento.getActividades().get(titulo);
     * }
     * }
     * return null;
     * }
     */
    /**
     * @brief realiza la solicitud de una actividad
     * @param nAcompanantes número entero de acompañantes
     * @param actividad Actividad para la que se realiza la solicitud
     * @param socio Socio que va a realizar la solicitud
     */
    void realizarSolicitud(int nAcompanantes, Actividad actividad, Socio socio) {
        if (temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo())==null){
            throw new NoHayActividades();
        }else {
            LocalDate fechaActual = LocalDate.now();
            Solicitud nuevaSolicitud = new Solicitud(nAcompanantes, fechaActual, socio);
            temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).realizarSolicitud(nuevaSolicitud);
        }
    }

    /**
     * @brief modifica el número de acompañantes que tendrá un socio
     * @param socio Socio
     * @param actividad Actividad
     * @param nAcompanantes número entero de acompañantes
     */
    void anadirAcompanante(@Valid Socio socio,@Valid Actividad actividad,@PositiveOrZero int nAcompanantes) {
        temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).getSolicitudes().get(socio.getEmail()).modificarAcompanantes(nAcompanantes);
    }

    /**
     * @brief modifica el número de acompañantes que tendrá un socio
     * @param socio Socio
     * @param actividad Actividad
     * @param nAcompanantes número entero de acompañantes
     */
    void quitarAcompanante(@Valid Socio socio,@Valid Actividad actividad,@PositiveOrZero int nAcompanantes) {
        temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).getSolicitudes().get(socio.getEmail()).modificarAcompanantes(nAcompanantes);
    }

    /**
     * @brief borra las solicitudes que realiza un socio a una actividad
     * @param actividad Actividad
     * @param socio Socio solicitante
     */
    void borrarSolicitud(@Valid Actividad actividad,@Valid Socio socio) {
        temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).borrarSolicitud(socio.getEmail());
    }


    void crearNuevaTemporada() {

    }
}
