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

        if (temporadaActual.buscarActividadPorTitulo(a.getTitulo()))
            throw new ActividadYaExistente();

        temporadas.getLast().crearActividad(a);
    }

    void revisarSolicitudes() {

    }

    void marcarCuotaPagada(@Valid Socio socio) {

        if (!socios.get(socio).isCuotaPagada()) {
            socios.get(socio).setCuotaPagada(true);
        } else {
            throw new PagoYaRealizado();
        }
    }

    /**
     * Actividad buscarActividad(@NotBlank String titulo,@Positive @PositiveOrZero int anio) {
     * for (Temporada elemento : temporada) {
     * if (elemento.getAnio()==anio) {
     * return elemento.getActividades().get(titulo);
     * }
     * }
     * return null;
     * }
     */

    Boolean realizarSolicitud(int nAcompanantes, Actividad actividad) {
        return null;
    }

    Boolean anadirAcompanante() {
        return null;
    }

    Boolean quitarAcompanante() {
        return null;
    }

    void borrarSolicitud(Actividad actividad) {

    }


    void crearNuevaTemporada() {

    }
}
