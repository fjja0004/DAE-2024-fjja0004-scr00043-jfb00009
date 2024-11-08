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

    /**
     * @param email email del socio
     * @param clave clave del socio
     * @return Socio que ha iniciado sesión
     * @brief Función que permite a un usuario iniciar sesión en el sistema.
     */
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
     * @param actividad Actividad que se crea
     * @brief creación de una actividad
     */
    void crearActividad(Socio direccion, @Valid Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        repositorioActividades.crear(actividad);
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
     * @return lista de actividades abiertas
     * @brief Busca todas las actividades a las que es posible inscribirse.
     */
    public List<Actividad> buscarActividadesAbiertas() {
        return repositorioActividades.buscaTodasActividadesAbiertas();
    }

    /**
     * @return lista de todas las actividades de la temporada actual
     * @brief Devuelve una lista con todas las actividades de la temporada actual
     */
    public List<Actividad> buscarTodasActividadesTemporadaActual() {
        return repositorioActividades.buscarTodasTemporadaActual();
    }

    /**
     * @param solicitante   Socio que va a realizar la solicitud
     * @param actividad     Actividad para la que se realiza la solicitud
     * @param nAcompanantes número entero de acompañantes
     * @brief realiza la solicitud de una actividad
     */
    public void realizarSolicitud(Socio solicitante, Actividad actividad, int nAcompanantes) {
        Socio socio = login(solicitante.getEmail(), solicitante.getClave());
        Actividad actSolicitada = repositorioActividades.buscarPorId(actividad.getId());
        Solicitud solicitud = new Solicitud(socio, nAcompanantes);
        actSolicitada.realizarSolicitud(solicitud);
    }

    /**
     * @param socio         Socio que va a realizar la modificación
     * @param actividad     Actividad a la que se va a modificar el número de acompañantes
     * @param nAcompanantes número entero de acompañantes
     * @throws NoHayActividades excepcion que se lanza en caso de que la actividad no exista
     * @brief modifica el número de acompañantes que tendrá un socio
     */
    public void modificarAcompanantes(Socio socio, Actividad actividad, int nAcompanantes) {
        Socio socioMod = login(socio.getEmail(), socio.getClave());
        Actividad actMod = repositorioActividades.buscarPorId(actividad.getId());
        actMod.modificarAcompanantes(socioMod.getEmail(), nAcompanantes);
    }

    /**
     * @param actividad actividad de la que se buscan las solicitudes
     * @param direccion Socio que realiza la operación
     * @return lista de solicitudes de la actividad
     * @brief Devuelve una lista con las solicitudes de una actividad
     */
    public List<Solicitud> buscarSolicitudesDeActividad(Socio direccion, Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();

        Actividad act = repositorioActividades.buscarPorId(actividad.getId());
        return act.getSolicitudes();
    }

    /**
     * @param socio     Socio que ha realizado la solicitud
     * @param actividad Actividad a la que se ha solicitado la inscripción
     * @brief Elimina la solicitud de inscripción de un socio a una actividad
     */
    public void cancelarSolicitud(Socio socio, Actividad actividad) {
        Socio socioCancel = login(socio.getEmail(), socio.getClave());
        Actividad actCancel = repositorioActividades.buscarPorId(actividad.getId());
        actCancel.cancelarSolicitud(socioCancel.getEmail());
    }

    /**
     * @brief acepta 1 acompañante más, se realiza 1 a 1
     * @param socio Socio del que se quiere añadir acompante
     * @param actividad Actividad de la que se va a añadir acompañante
     */
    public void aceptarAcompanante(Socio socio,Actividad actividad){
        Optional<Solicitud> solicitudOptional=repositorioActividades.buscarPorId(actividad.getId()).buscarSolicitudPorEmail(socio.getEmail());
        if (solicitudOptional.isPresent()){
            solicitudOptional.get().setPlazasAceptadas(solicitudOptional.get().getPlazasAceptadas()+1);
        }

    }

    //TODO: COMPLETAR
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
