package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import es.ujaen.dae.clubsocios.excepciones.OperacionDeDireccion;
import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubsocios.repositorios.RepositorioActividades;
import es.ujaen.dae.clubsocios.repositorios.RepositorioSocios;
import es.ujaen.dae.clubsocios.repositorios.RepositorioTemporadas;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

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
        return socio.getEmail().equals(admin.getEmail()) && socio.getClave().equals(admin.getClave());
    }

    /**
     * @param email email del socio
     * @param clave clave del socio
     * @return Socio que ha iniciado sesión
     * @throws SocioNoValido en caso de que el socio no exista
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
     * Busca un socio por su email.
     *
     * @param email email del socio que se busca. Debe ser un email válido y no nulo.
     * @return un Optional con el socio si existe.
     * @throws SocioNoValido si el socio no existe.
     */
    public Optional<Socio> buscarSocio(@NotNull @Email String email) {
        if (email.equals(admin.getEmail()))
            return Optional.of(admin);

        return repositorioSocios.buscar(email);
    }

    /**
     * @param socio Socio que se va a registrar
     * @throws SocioYaRegistrado en caso de que sea el mismo que el administrador
     * @throws SocioYaRegistrado en caso de que ya esté registrado
     * @brief añade un nuevo socio
     */
    public void crearSocio(@Valid Socio socio) {
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
    public void crearActividad(@Valid Actividad actividad) {
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
     * @brief crea la solicitud de una actividad
     */
    @Transactional
    public Solicitud crearSolicitud(Socio solicitante, Actividad actividad, int nAcompanantes) {
        solicitante = repositorioSocios.buscar(solicitante.getEmail()).get();
        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        Solicitud solicitud = new Solicitud(solicitante, nAcompanantes);
        actividad.crearSolicitud(solicitud);
        repositorioActividades.guardarSolicitud(solicitud);
        return solicitud;
    }

    /**
     * @param actividad actividad de la que se buscan las solicitudes
     * @param direccion Socio que realiza la operación
     * @return lista de solicitudes de la actividad
     * @brief Devuelve una lista con las solicitudes de una actividad
     */
    @Transactional
    public List<Solicitud> buscarSolicitudesDeActividad(Socio direccion, Actividad actividad) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        return actividad.getSolicitudes();
    }

    /**
     * @param actividad actividad de la que se va a cancelar la solicitud
     * @param solicitud Solicitud que se desea cancelar
     * @brief Elimina la solicitud de inscripción de un socio a una actividad
     */
    @Transactional
    public void cancelarSolicitud(Actividad actividad, Solicitud solicitud) {
        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        actividad.cancelarSolicitud(solicitud);
        repositorioActividades.borrarSolicitud(solicitud);
    }

    /**
     * @param actividad     Actividad a la que se va a modificar el número de acompañantes
     * @param solicitud     Solicitud que se va a modificar
     * @param nAcompanantes número entero de acompañantes
     * @throws NoHayActividades excepcion que se lanza en caso de que la actividad no exista
     * @brief modifica el número de acompañantes que tendrá un socio
     */
    @Transactional
    public Solicitud modificarSolicitud(Actividad actividad, Solicitud solicitud, int nAcompanantes) {
        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        Solicitud solicitudActualizada = actividad.modificarAcompanantes(solicitud, nAcompanantes);
        repositorioActividades.actualizarSolicitud(solicitudActualizada);
        return solicitudActualizada;
    }

    /**
     * @param direccion Miembro de la dirección que realiza la operación
     * @param actividad Actividad a la que se solicita la inscripción
     * @param solicitud Solicitud a la que se va a asignar una plaza
     * @brief Acepta una de las plazas solicitadas por un socio
     */
    @Transactional
    public Solicitud asignarPlaza(Socio direccion, Actividad actividad, Solicitud solicitud) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();

        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        solicitud = actividad.aceptarPlaza(solicitud);
        repositorioActividades.actualizar(actividad);
        repositorioActividades.actualizarSolicitud(solicitud);
        return solicitud;
    }

    /**
     * @param direccion Miembro de la dirección que realiza la operación
     * @param actividad Actividad a la que se solicita la inscripción
     * @param solicitud Solicitud a la que se va a quitar una plaza
     * @brief Quita una de las plazas aceptadas a una solicitud
     */
    @Transactional
    public Solicitud quitarPlaza(Socio direccion, Actividad actividad, Solicitud solicitud) {
        if (!esAdmin(direccion))
            throw new OperacionDeDireccion();
        actividad = repositorioActividades.buscarPorId(actividad.getId()).get();
        solicitud = actividad.quitarPlaza(solicitud);
        repositorioActividades.actualizar(actividad);
        repositorioActividades.actualizarSolicitud(solicitud);
        return solicitud;
    }
}
