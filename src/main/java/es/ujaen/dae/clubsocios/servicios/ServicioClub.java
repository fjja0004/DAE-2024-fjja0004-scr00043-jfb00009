package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.IntentoBorrarAdmin;
import es.ujaen.dae.clubsocios.excepciones.SocioNoRegistrado;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
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
    private final ArrayList<Temporada> temporada;

    // Socio especial que representa al administrador del club
    private static final Socio admin = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");

    public ServicioClub() {
        socios = new HashMap<>();
        temporada = new ArrayList<>();
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

        //TODO borrar solicitudes del socio, si son para actividades que no se han celebrado
    }

    Boolean anadirActividad(@NotBlank String titulo, String descripcion, int precio, int nPlazas, @FutureOrPresent LocalDate fechaCelebracion, LocalDate fechaInscripcion) {

        if (temporada.get(temporada.size()).getActividades().containsKey(titulo)) {

            return false;
        } else {
            Actividad nuevaActividad = new Actividad(titulo, descripcion, precio, nPlazas, fechaCelebracion, fechaInscripcion, fechaCelebracion);
            //¿ seria la ultima temporada , es decir , la actual?
            temporada.get(temporada.size()).anadirNuevaActividad(nuevaActividad);
            return true;
        }
    }

    Boolean borrarActividad(String titulo) {
        return null;
    }

    void revisarSolicitudes() {

    }

    void marcarCuotaPagada(Socio socio) {

    }

    Actividad buscarActividad(@NotBlank String titulo,@NotBlank int anio) {
        for (Temporada elemento : temporada) {
            if (elemento.getAnio()==anio) {
                return elemento.getActividades().get(titulo);
            }
        }
        return null;
    }

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

    void pagarCuota(Socio socio) {

    }

    void crearNuevaTemporada(Socio socio) {

    }
}
