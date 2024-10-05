package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class ServicioClub {
    private Map<String, Socio> socios = new HashMap<String, Socio>();
    private ArrayList<Temporada> temporada = new ArrayList<>();

    // Socio especial que representa al administrador del club
    private static final Socio admin = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");

    public Optional<Socio> login(String email, String clave) {

        if (admin.getEmail().equals(email) && admin.getClave().equals(clave))
            return Optional.of(admin);

        Socio socio = socios.get(email);
        return (socio != null && socio.getClave().equals(clave)) ? Optional.of(socio): Optional.empty();


    }

    public void anadirSocio(@Valid Socio socio) {
        // Evitar que se cree un usuario con la cuenta de direccion
        if (socio.getEmail().equals(admin.getEmail()))
            throw new SocioYaRegistrado();

        if (socios.containsKey(socio.getEmail()))
            throw new SocioYaRegistrado();

        socios.put(socio.getEmail(), socio);

    }

    Boolean borrarSocio(String email) {
        return null;
    }

    Boolean anadirActividad(String titulo, String descripcion, double precio, int nPlazas, Date fechaCelebracion, Date fechaInscripcion) {
        return null;
    }

    Boolean borrarActividad(String titulo) {
        return null;
    }

    void revisarSolicitudes() {

    }

    void marcarCuotaPagada(Socio socio) {

    }

    Actividad buscarActividad(String titulo, int anio) {
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
