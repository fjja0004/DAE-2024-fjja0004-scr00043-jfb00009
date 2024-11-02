package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.objetosValor.Actividad;
import es.ujaen.dae.clubsocios.objetosValor.Solicitud;
import es.ujaen.dae.clubsocios.repositorios.RepositorioActividades;
import es.ujaen.dae.clubsocios.repositorios.RepositorioSocios;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    RepositorioSocios repositorioSocios;
    @Autowired
    RepositorioActividades repositorioActividades;

    private final ArrayList<Temporada> temporadas;

    // Socio especial que representa al administrador del club
    private static final Socio admin = new Socio("administrador", "-", "admin@club.es", "666666666", "ElAdMiN");

    /**
     * @brief constructor por defecto de la clase ServicioClub
     */
    public ServicioClub() {
        repositorioSocios = new RepositorioSocios();
        repositorioActividades = new RepositorioActividades();
        temporadas = new ArrayList<>();
        temporadas.add(new Temporada(LocalDate.now().getYear()));
    }

    /**
     * @param socio socio a comprobar.
     * @return true si es admin, false si no es admin.
     * @brief Función que comprueba si un usuario es administrador.
     */
    boolean esAdmin(Socio socio) {
        if (socio.getEmail().equals(admin.getEmail()) && socio.getClave().equals(admin.getClave())) {
            return true;
        }
        return false;
    }

    public Socio login(@Email String email, String clave) {

        if (admin.getEmail().equals(email) && admin.comprobarCredenciales(clave)) {
            return admin;
        }

        Socio socio = repositorioSocios.buscarPorEmail(email);
        socio.comprobarCredenciales(clave);
        return socio;
    }

    /**
     * @param socio Socio
     * @throws SocioYaRegistrado en caso de que sea el mismo que el administrador
     * @throws SocioYaRegistrado en caso de que ya esté registrado
     * @brief añade un nuevo socio
     */
    public void anadirSocio(@Valid Socio socio) {
        if (esAdmin(socio)) {
            throw new SocioYaRegistrado();
        }
        repositorioSocios.crear(socio);
    }

    /**
     * @param a Actividad que se crea
     * @brief creación de una actividad
     */
    void crearActividad(Socio direccion, @Valid Actividad a) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        repositorioActividades.crear(a);
    }

    /**
     * @param socio Socio que paga la cuota
     * @brief marca la cuota del socio como pagada, en caso de que ya esté pagado lanza una excepción
     */
    public void marcarCuotaPagada(Socio direccion, @Valid Socio socio) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        if (!repositorioSocios.buscarPorEmail(socio.getEmail()).isCuotaPagada()) {
            repositorioSocios.buscarPorEmail(socio.getEmail()).setCuotaPagada(true);
        } else {
            throw new PagoYaRealizado();
        }
    }

    /**
     * @param actividad    actividad a la que se ha solicitado la inscripción
     * @param solicitante  socio que ha realizado la solicitud
     * @param acompanantes número de acompañantes aceptados
     * @brief Marca como aceptada una solicitud a una actividad
     */
    //@todo completar est y submetodos si es necesario
    public void aceptarSolicitud(Socio direccion, Socio socio, @Valid Actividad actividad, String solicitante, int acompanantes) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        buscarActividad(actividad.getTitulo()).aceptarSolicitud(solicitante, acompanantes);
    }

    //TODO: completar buscarTodasActividadesPorTemporada y buscarActividadesAbiertas, borrar buscarActividad

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
     * @return lista de actividades abiertas
     * @brief Busca todas las actividades a las que es posible inscribirse.
     */
    List<Actividad> buscarActividadesAbiertas() {
        return repositorioActividades.buscaTodasActividadesAbiertas();
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
            Solicitud nuevaSolicitud = new Solicitud(nAcompanantes, socio);
            temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).realizarSolicitud(nuevaSolicitud);
        }
    }

    /**
     * @param socio         Socio que va a realizar la modificación
     * @param actividad     Actividad a la que se va a modificar el número de acompañantes
     * @param nAcompanantes número entero de acompañantes
     * @throws NoHayActividades excepcion que se lanza en caso de que la actividad no exista
     * @brief modifica el número de acompañantes que tendrá un socio
     */
    void modificarAcompanantes(Socio socio, Actividad actividad, int nAcompanantes) {
        if (temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()) == null) {
            throw new NoHayActividades();
        } else {
            temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).modificarAcompanantes(socio.getEmail(), nAcompanantes);
        }
    }

    /**
     * @param actividad Actividad
     * @param socio     Socio solicitante
     * @throws NoHayActividades excepcion que se lanza si no esta la actividad registrada
     * @brief borra las solicitudes que realiza un socio a una actividad
     */
    void borrarSolicitud(@Valid Actividad actividad, @Valid Socio socio) {
        if (temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()) == null) {
            throw new NoHayActividades();
        } else {
            temporadas.getLast().buscarActividadPorTitulo(actividad.getTitulo()).borrarSolicitud(socio.getEmail());
        }
    }

    /**
     * @brief crea una nueva temporada al inicio de cada año
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    void crearNuevaTemporada() {
        Temporada nuevaTemporada = new Temporada(LocalDate.now().getYear());
        if (!temporadas.contains(nuevaTemporada)) {
            temporadas.add(new Temporada(LocalDate.now().getYear()));
            //poner todos los socios con la cuota no pagada - false
            for (Socio socio : repositorioSocios.buscaTodos()) {
                socio.setCuotaPagada(false);
            }
        } else {
            throw new TemporadaYaExistente();
        }
    }
}
