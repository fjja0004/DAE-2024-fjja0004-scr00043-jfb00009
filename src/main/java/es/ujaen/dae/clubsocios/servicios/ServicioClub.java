package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.*;
import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.repositorios.RepositorioActividades;
import es.ujaen.dae.clubsocios.repositorios.RepositorioSocios;
import es.ujaen.dae.clubsocios.repositorios.RepositorioTemporadas;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Repository
@Validated
public class ServicioClub {
    @Autowired
    RepositorioSocios repositorioSocios;
    @Autowired
    RepositorioActividades repositorioActividades;
    @Autowired
    RepositorioTemporadas repositorioTemporadas;

    // Socio especial que representa al administrador del club
    private static final Socio admin = new Socio("administrador", "-", "admin@club.com", "666666666", "admin");

    /**
     * @brief constructor por defecto de la clase ServicioClub
     */
    public ServicioClub() {
        repositorioSocios = new RepositorioSocios();
        repositorioActividades = new RepositorioActividades();
        repositorioTemporadas = new RepositorioTemporadas();
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

        if (repositorioSocios.buscar(email).isPresent()) {
            Socio socio = repositorioSocios.buscar(email).get();
            socio.comprobarCredenciales(clave);
            return socio;
        } else {
            throw new SocioNoValido();
        }
    }

    /**
     * @param socio Socio que se va a registrar
     * @throws SocioYaRegistrado en caso de que sea el mismo que el administrador
     * @throws SocioYaRegistrado en caso de que ya esté registrado
     * @brief añade un nuevo socio
     */
    public void nuevoSocio(@Valid Socio socio) {
        if (esAdmin(socio)) {
            throw new SocioNoValido();
        }
        repositorioSocios.guardar(socio);
    }

    /**
     * @return lista de todos los socios
     * @brief Busca todos los socios
     */
    public List<Socio> buscarTodosSocios(Socio direccion) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        return repositorioSocios.buscarTodos();
    }

    /**
     * @param direccion Miembro de la dirección que realiza la operación
     * @param socio     Socio que paga la cuota
     * @brief marca la cuota del socio como pagada
     */
    public void marcarCuotaPagada(Socio direccion, @Valid Socio socio) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        repositorioSocios.marcarCuotaPagada(socio);
    }

    /**
     * @brief Crea una temporada al iniciar la aplicación
     */
    @PostConstruct
    void crearTemporadaInicial() {
        crearNuevaTemporada();
    }

    /**
     * @brief Crea una nueva temporada al inicio de cada año
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    void crearNuevaTemporada() {
        repositorioTemporadas.crearTemporada();
        repositorioSocios.marcarTodasCuotasNoPagadas();
    }

    /**
     * @param anio año de la temporada
     * @return temporada con el año dado
     * @brief Busca una temporada por su año
     */
    public Optional<Temporada> buscarTemporadaPorAnio(int anio) {
        return repositorioTemporadas.buscar(anio);
    }

    /**
     * @return lista de todas las temporadas
     * @brief Busca todas las temporadas
     */
    public List<Temporada> buscarTodasTemporadas() {
        return repositorioTemporadas.buscarTodasTemporadas();
    }

    /**
     * @param actividad Actividad que se crea
     * @brief creación de una actividad
     */
    @Transactional
    public void crearActividad(Socio direccion, @Valid Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        repositorioTemporadas.temporadaActual().nuevaActividad(actividad);
        repositorioActividades.guardarActividad(actividad);
    }

    /**
     * @param id id de la actividad
     * @return Actividad con el id dado
     * @brief Busca una actividad por su id
     */
    public Optional<Actividad> buscarActividadPorId(int id) {
        return repositorioActividades.buscarPorId(id);
    }

    /**
     * @return lista de actividades abiertas
     * @brief Busca todas las actividades a las que es posible inscribirse.
     */
    public List<Actividad> buscarActividadesAbiertas() {
        return repositorioActividades.buscaTodasActividadesAbiertas();
    }

    /**
     * @param anio año de la temporada
     * @return lista de todas las actividades de la temporada dada
     * @brief Devuelve una lista con todas las actividades de la temporada dada
     */
    public List<Actividad> buscarActividadesTemporada(int anio) {
        return repositorioTemporadas.buscar(anio).get().getActividades();
    }

    /**
     * @param actividad Actividad a modificar.
     * @implNote Esta función se utiliza únicamente para testear otras operaciones,
     * no está preparada para que se utilice desde el frontend.
     * @brief Modifica la(s) fecha(s) de una actividad por la que se pasa como parámetro.
     */
    protected void modificarFechaActividad(Actividad actividad) {
        repositorioActividades.modificarFechaActividad(actividad);
    }

    /**
     * @param solicitante   Socio que va a realizar la solicitud
     * @param actividad     Actividad para la que se realiza la solicitud
     * @param nAcompanantes número entero de acompañantes
     * @brief realiza la solicitud de una actividad
     */
    public void realizarSolicitud(Socio solicitante, Actividad actividad, int nAcompanantes) {
        Socio socio = login(solicitante.getEmail(), solicitante.getClave());
        if (socio.isCuotaPagada()) {
            if (actividad.isAbierta()) {
                if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
                    Actividad actSolicitada = repositorioActividades.buscarPorId(actividad.getId()).get();
                    repositorioActividades.guardarSolicitud(socio, nAcompanantes, actSolicitada);
                }
            } else {
                throw new InscripcionCerrada();
            }
        } else {
            throw new SocioNoValido();
        }
    }

    /**
     * @param socio         Socio que va a realizar la modificación
     * @param actividad     Actividad a la que se va a modificar el número de acompañantes
     * @param nAcompanantes número entero de acompañantes
     * @throws NoHayActividades excepcion que se lanza en caso de que la actividad no exista
     * @brief modifica el número de acompañantes que tendrá un socio
     */
    public void modificarAcompanantes(Socio socio, Actividad actividad, int nAcompanantes) {
        if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
            repositorioActividades.modificarAcompanantes(actividad, socio, nAcompanantes);
        }
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

        if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
            Actividad act = repositorioActividades.buscarPorId(actividad.getId()).get();
            return act.getSolicitudes();
        } else {
            throw new NoHayActividades();
        }
    }

    /**
     * @param direccion Socio que realiza la operación
     * @param actividad Actividad de la que se busca la solicitud
     * @param id        id de la solicitud
     * @return Solicitud con el id dado
     * @brief Busca una solicitud por su id
     */
    public Optional<Solicitud> buscarSolicitudPorId(Socio direccion, Actividad actividad, int id) {
        List<Solicitud> solicitudes = buscarSolicitudesDeActividad(direccion, actividad);
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == id) {
                return Optional.of(solicitud);
            }
        }
        return Optional.empty();
    }

    /**
     * @param socio     Socio que ha realizado la solicitud
     * @param actividad Actividad a la que se ha solicitado la inscripción
     * @brief Elimina la solicitud de inscripción de un socio a una actividad
     */
    public void cancelarSolicitud(Socio socio, Actividad actividad) {
        Socio socioCancel = login(socio.getEmail(), socio.getClave());
        if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
            Actividad actCancel = repositorioActividades.buscarPorId(actividad.getId()).get();
            repositorioActividades.cancelarSolicitud(socioCancel, actCancel);
        } else {
            throw new NoHayActividades();
        }
    }

    /**
     * @param direccion Miembro de la dirección que realiza la operación
     * @param socio     Socio que realiza la solicitud de inscripción
     * @param actividad Actividad a la que se solicita la inscripción
     * @brief Acepta una de las plazas solicitadas por un socio
     */
    public void asignarPlaza(Socio direccion, Socio socio, Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        Socio solicitante = login(socio.getEmail(), socio.getClave());
        if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
            repositorioActividades.buscarPorId(actividad.getId()).get().aceptarPlaza(solicitante.getEmail());
        }
    }

    /**
     * @param direccion Miembro de la dirección que realiza la operación
     * @param socio     Socio que realiza la solicitud de inscripción
     * @param actividad Actividad a la que se solicita la inscripción
     * @brief Quita una de las plazas aceptadas a una solicitud
     */
    public void quitarPlaza(Socio direccion, Socio socio, Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        Socio solicitante = login(socio.getEmail(), socio.getClave());
        if (repositorioActividades.buscarPorId(actividad.getId()).isPresent()) {
            Actividad actividadSolicitada = repositorioActividades.buscarPorId(actividad.getId()).get();
            Optional<Solicitud> solicitud = actividadSolicitada.quitarPlaza(solicitante.getEmail());
            repositorioActividades.actualizar(actividadSolicitada);
            repositorioActividades.actualizar(solicitud.get());
        }
    }
}
