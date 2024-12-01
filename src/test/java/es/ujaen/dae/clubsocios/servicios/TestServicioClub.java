package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.excepciones.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.clubsocios.app.ClubSocios.class)
@ActiveProfiles("test")
public class TestServicioClub {

    @Autowired
    private ServicioClub servicioClub;

    @BeforeEach
    public void setUp() {
        servicioClub.crearSocio(new Socio("Socio", "Prueba", "socio_prueba@club.com", "621302025", "password123"));
    }

    @Test
    @DirtiesContext
    public void testLogin() {
        // Verifica el login con el administrador.
        assertEquals("admin@club.com", servicioClub.login("admin@club.com", "admin").getEmail());

        // Verifica el login con el administrador pero con la contraseña incorrecta.
        assertThatThrownBy(() -> servicioClub.login("admin@club.es", "wrongpassword")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con un socio válido
        assertEquals("socio_prueba@club.com", servicioClub.login("socio_prueba@club.com", "password123").getEmail());

        // Comprobamos valores nulos para email.
        assertThatThrownBy(() -> servicioClub.login("", "password123")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con una contraseña incorrecta.
        assertThatThrownBy(() -> servicioClub.login("socio_prueba@club.com", "wrongpassword")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con un email no registrado.
        assertThatThrownBy(() -> servicioClub.login("noexiste@club.com", "password123")).isInstanceOf(SocioNoValido.class);

        // Comprobamos que se pueda registrar un nuevo socio y que pueda hacer login.
        Socio socioNuevo = new Socio("Socio", "-", "socio_nuevo@club.com", "623456789", "password123");
        servicioClub.crearSocio(socioNuevo);
        assertEquals("socio_nuevo@club.com", servicioClub.login(socioNuevo.getEmail(), socioNuevo.getClave()).getEmail());
    }

    @Test
    @DirtiesContext
    public void testEsAdmin() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Comprobamos que devuelva true si el socio es el administrador.
        assertTrue(servicioClub.esAdmin(admin));

        //Comprobamos que devuelva false si el socio no es el administrador.
        assertFalse(servicioClub.esAdmin(socio));
    }

    @Test
    @DirtiesContext
    void testCrearSocio() {
        Socio admin = servicioClub.login("admin@club.com", "admin");

        //Comprobamos que no se pueda añadir un socio igual al administrador.
        assertThatThrownBy(() -> servicioClub.crearSocio(admin)).isInstanceOf(SocioNoValido.class);

        //Comprobamos que no se pueda añadir un socio igual al otro usuario ya registrado.
        Socio socioRepetido = servicioClub.login("socio_prueba@club.com", "password123");
        assertThrows(SocioYaRegistrado.class, () -> servicioClub.crearSocio(socioRepetido));

        //Comprobamos que se pueda añadir un socio no registrado.
        Socio socioNoRegistrado = new Socio("Socio", "-", "socio_no_registrado@club.com", "623456789", "password123");
        assertDoesNotThrow(() -> servicioClub.crearSocio(socioNoRegistrado));
    }

    @Test
    @DirtiesContext
    void testBuscarTodosSocios() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");
        Socio socioNuevo = new Socio("Socio", "-", "socio_nuevo@club.com", "623456789", "password123");

        servicioClub.crearSocio(socioNuevo);

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.buscarTodosSocios(socio)).isInstanceOf(OperacionDeDireccion.class);

        //Verificamos que se devuelvan todos los socios.
        assertEquals(2, servicioClub.buscarTodosSocios(admin).size());
    }

    @Test
    @DirtiesContext
    void testMarcarCuotaPagada() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Compruebamos que se hace como administrador.
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(socio, admin)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que funcione con los datos correctos.
        assertDoesNotThrow(() -> servicioClub.marcarCuotaPagada(admin, socio));

        //Comprobamos que se ha marcado la cuota como pagada.
        assertTrue(servicioClub.login(socio.getEmail(), socio.getClave()).isCuotaPagada());
    }


    @Test
    @DirtiesContext
    void testCrearTemporadaInicial() {
        //Verificamos que se crea la temporada inicial.
        assertEquals(LocalDate.now().getYear(), servicioClub.buscarTemporadaPorAnio(LocalDate.now().getYear()).get().getAnio());
    }

    @Test
    @DirtiesContext
    void testCrearNuevaTemporada() {
        //Verificamos que no se cree una nueva temporada si ya existe.
        int numeroTemporadas = servicioClub.buscarTodasTemporadas().size();
        servicioClub.crearNuevaTemporada();
        assertEquals(numeroTemporadas, servicioClub.buscarTodasTemporadas().size());

        //Verificamos que se ha creado la temporada del año actual.
        assertEquals(LocalDate.now().getYear(), servicioClub.buscarTemporadaPorAnio(LocalDate.now().getYear()).get().getAnio());
    }

    @Test
    @DirtiesContext
    void testBuscarTemporadaPorAnio() {
        //Verificamos que no se devuelva la temporada si no existe.
        assertEquals(Optional.empty(), servicioClub.buscarTemporadaPorAnio(2022));

        //Verificamos que se devuelva la temporada si existe.
        assertEquals(LocalDate.now().getYear(), servicioClub.buscarTemporadaPorAnio(LocalDate.now().getYear()).get().getAnio());
    }

    @Test
    @DirtiesContext
    void testBuscarTodasTemporadas() {
        //Verificamos que se devuelvan todas las temporadas.
        assertEquals(1, servicioClub.buscarTodasTemporadas().size());
    }

    @Test
    @DirtiesContext
    void testCrearActividad() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        Actividad actividadNoValida = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(7), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(1));

        Actividad actividadNoValida2 = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(1));

        //Comprobaciones para actividades no válidas.
        assertThatThrownBy(() -> servicioClub.crearActividad(admin, actividadNoValida)).isInstanceOf(FechaNoValida.class);
        assertThatThrownBy(() -> servicioClub.crearActividad(admin, actividadNoValida2)).isInstanceOf(FechaNoValida.class);

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.crearActividad(socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        //Se añade correctamente la actividad.
        assertDoesNotThrow(() -> servicioClub.crearActividad(admin, actividad));

        //Comprobamos que se ha añadido la actividad.
        assertEquals(actividad.getTitulo(), servicioClub.buscarActividadPorId(actividad.getId()).get().getTitulo());
    }

    @Test
    @DirtiesContext
    void testBuscarActividadPorId() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que no devuelva la actividad si no existe.
        assertEquals(Optional.empty(), servicioClub.buscarActividadPorId(actividad.getId()));

        servicioClub.crearActividad(admin, actividad);

        //Comprobamos que devuelva la actividad si existe.
        assertEquals(actividad.getId(), servicioClub.buscarActividadPorId(actividad.getId()).get().getId());
    }

    @Test
    @DirtiesContext
    void buscarActividadesAbiertas() {
        Socio admin = servicioClub.login("admin@club.com", "admin");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(10));

        //Comprobamos que la lista esté vacía si no hay actividades abiertas.
        assertEquals(0, servicioClub.buscarActividadesAbiertas().size());

        //Si hay alguna, pero todas están cerradas.
        servicioClub.crearActividad(admin, actividadCerrada);
        assertEquals(0, servicioClub.buscarActividadesAbiertas().size());

        //Comprobamos que se devuelva la actividad abierta.
        servicioClub.crearActividad(admin, actividadAbierta);
        assertEquals(1, servicioClub.buscarActividadesAbiertas().size());
        assertEquals(actividadAbierta.getId(), servicioClub.buscarActividadesAbiertas().getLast().getId());
        //TODO: quitar assertTrue(servicioClub.buscarActividadesAbiertas().contains(actividadAbierta));
    }

    @Test
    @DirtiesContext
    void testBuscarActividadesTemporada() {
        Socio admin = servicioClub.login("admin@club.com", "admin");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que no se devuelvan actividades si no hay.
        assertEquals(0, servicioClub.buscarActividadesTemporada(LocalDate.now().getYear()).size());

        servicioClub.crearActividad(admin, actividad);
        //Comprobamos que se devuelvan las actividades de la temporada actual.
        assertEquals(1, servicioClub.buscarActividadesTemporada(LocalDate.now().getYear()).size());
        assertEquals(actividad.getId(), servicioClub.buscarActividadesTemporada(LocalDate.now().getYear()).getLast().getId());
    }

    @Test
    @DirtiesContext
    void testModificarFechaActividad() {
        Socio admin = servicioClub.login("admin@club.com", "admin");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(admin, actividad);

        //Comprobamos que modifique la fecha de inicio de inscripción.
        LocalDate nuevaFechaInicio = LocalDate.now().plusDays(1);
        actividad.setFechaInicioInscripcion(nuevaFechaInicio);
        servicioClub.modificarFechaActividad(actividad);
        assertEquals(nuevaFechaInicio, servicioClub.buscarActividadPorId(actividad.getId()).get().getFechaInicioInscripcion());

        //Comprobamos que modifique la fecha de fin de inscripción.
        LocalDate nuevaFechaFin = LocalDate.now().plusDays(8);
        actividad.setFechaFinInscripcion(nuevaFechaFin);
        servicioClub.modificarFechaActividad(actividad);
        assertEquals(nuevaFechaFin, servicioClub.buscarActividadPorId(actividad.getId()).get().getFechaFinInscripcion());

        //Comprobamos que se modifique la fecha de celebración.
        LocalDate nuevaFechaCelebracion = LocalDate.now().plusDays(11);
        actividad.setFechaCelebracion(nuevaFechaCelebracion);
        servicioClub.modificarFechaActividad(actividad);
        assertEquals(nuevaFechaCelebracion, servicioClub.buscarActividadPorId(actividad.getId()).get().getFechaCelebracion());
    }

    @Test
    @DirtiesContext
    void testCrearSolicitud() {
        Socio direccion = servicioClub.login("admin@club.com", "admin");
        Socio socioSinCuota = servicioClub.login("socio_prueba@club.com", "password123");
        Socio socioConCuota = new Socio("Socio", "Prueba", "socio_cuota@club.com", "621302025", "password123");

        servicioClub.crearSocio(socioConCuota);
        servicioClub.marcarCuotaPagada(direccion, socioConCuota);

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(direccion, actividadCerrada);

        //Comprobamos que se lance una excepción si el socio no está registrado.
        //assertThatThrownBy(() -> servicioClub.crearSolicitud(new Socio(), actividadCerrada, 3)).isInstanceOf(SocioNoValido.class);

        //Comprobamos que se lance una excepción si la actividad no está abierta.
        assertThatThrownBy(() -> servicioClub.crearSolicitud(socioSinCuota, actividadCerrada, 3)).isInstanceOf(InscripcionCerrada.class);

        servicioClub.crearActividad(direccion, actividadAbierta);

        Solicitud solicitud = servicioClub.crearSolicitud(socioSinCuota, actividadAbierta, 3);

        //Comprobamos que no aumenten las plazas ocupadas si el socio no ha pagado la cuota.
        assertEquals(0, servicioClub.buscarActividadPorId(actividadAbierta.getId()).get().getPlazasOcupadas());

        //Comprobamos que no aumenten las plazas aceptadas si el socio no ha pagado la cuota.
        assertEquals(0, solicitud.getPlazasAceptadas());

        Solicitud solicitudConCuota = servicioClub.crearSolicitud(servicioClub.login(socioConCuota.getEmail(),socioConCuota.getClave()), actividadAbierta, 3);

        //Comprobamos que aumenten las plazas ocupadas si el socio ha pagado la cuota.
        assertEquals(1, servicioClub.buscarActividadPorId(actividadAbierta.getId()).get().getPlazasOcupadas());

        //Comprobamos que aumenten las plazas aceptadas si el socio ha pagado la cuota.
        assertEquals(1, solicitudConCuota.getPlazasAceptadas());

        //Comprobamos que se lance una excepción si la solicitud ya se ha realizado.
        assertThatThrownBy(() -> servicioClub.crearSolicitud(socioSinCuota, actividadAbierta, 3)).isInstanceOf(SolicitudYaRealizada.class);
        assertThatThrownBy(() -> servicioClub.crearSolicitud(socioConCuota, actividadAbierta, 3)).isInstanceOf(SolicitudYaRealizada.class);
    }

    @Test
    @DirtiesContext
    void testModificarAcompanantes() {
        Socio direccion = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.marcarCuotaPagada(direccion, socio);
        servicioClub.crearActividad(direccion, actividadAbierta);

        for (Actividad actividad : servicioClub.buscarActividadesAbiertas()) {
            if (actividadAbierta.getTitulo().equals(actividad.getTitulo())) {
                actividadAbierta = actividad;
                break;
            }
        }

        //Comprobamos que se lance una excepción si la solicitud no existe.
        Actividad finalActividadAbierta = actividadAbierta; //para que funcionene las funciones lambda
        assertThatThrownBy(() -> servicioClub.modificarAcompanantes(socio, finalActividadAbierta, 3)).isInstanceOf(SolicitudNoExistente.class);

        //Comprobamos que no se lance una excepción si la modificación es correcta.
        servicioClub.crearSolicitud(socio, actividadAbierta, 3);
        assertDoesNotThrow(() -> servicioClub.modificarAcompanantes(socio, finalActividadAbierta, 5));

        //Comprobamos que se haya modificado el número de acompañantes.
        Solicitud solicitud = new Solicitud();
        for (Solicitud s : servicioClub.buscarSolicitudesDeActividad(direccion, finalActividadAbierta)) {
            if (s.getSocio().equals(socio)) {
                solicitud = s;
            }
        }
        assertEquals(5, solicitud.getnAcompanantes());

    }

    @Test
    @DirtiesContext
    void buscarSolicitudesDeActividad() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");
        Socio socioTest = servicioClub.login("socio_prueba@club.com", "password123");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio no es el administrador.
        assertThatThrownBy(() -> servicioClub.buscarSolicitudesDeActividad(socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.buscarSolicitudesDeActividad(direccion, actividad)).isInstanceOf(NoHayActividades.class);

        servicioClub.crearActividad(direccion, actividad);
        assertDoesNotThrow(() -> servicioClub.buscarSolicitudesDeActividad(direccion, actividad));

        //Comprobamos que devuelva una lista vacía si no hay solicitudes.
        assertEquals(0, servicioClub.buscarSolicitudesDeActividad(direccion, actividad).size());

        //Comprobamos que devuelva una lista con las solicitudes realizadas.
        servicioClub.marcarCuotaPagada(direccion, socioTest);
        servicioClub.crearSolicitud(socioTest, actividad, 3);
        assertEquals(1, servicioClub.buscarSolicitudesDeActividad(direccion, actividad).size());

    }

    @Test
    @DirtiesContext
    void testCancelarSolicitud() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(SocioNoValido.class);

        servicioClub.crearSocio(socio);
        servicioClub.marcarCuotaPagada(direccion, socio);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(NoHayActividades.class);

        servicioClub.crearActividad(direccion, actividad);
        servicioClub.crearActividad(direccion, actividadCerrada);

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(SolicitudNoExistente.class);
        servicioClub.crearSolicitud(socio, actividad, 3);

        //Comprobamos que no permita cancelar la solicitud si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividadCerrada)).isInstanceOf(InscripcionCerrada.class);

        //Comprobamos que se haya cancelado la solicitud.
        assertDoesNotThrow(() -> servicioClub.cancelarSolicitud(socio, actividad));

        //Nota: debuggeando se ve que la solicitud se elimina correctamente, pero al volver a cancelarla, la actividad no está actualizada y no se lanza la excepción.
        Actividad actividad1 = new Actividad();
        for (Actividad actividadAux : servicioClub.buscarActividadesAbiertas()) {
            if (actividadAux.getId() == actividad.getId()) {
                actividad1 = actividadAux;
            }
        }
        Actividad actividad2 = actividad1;
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad2)).isInstanceOf(SolicitudNoExistente.class);
    }

    @Test
    @DirtiesContext
    void testAsignarPlaza() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");
        Socio socio2 = new Socio("Socio2", "Prueba", "socio2@gmail.com", "621342025", "password124");
        Socio socio3 = new Socio("Socio3", "Prueba", "socio3@gmail.com", "623342025", "password125");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(socio, socio, actividad)).isInstanceOf(OperacionDeDireccion.class);


        servicioClub.crearSocio(socio);
        servicioClub.crearSocio(socio2);
        servicioClub.crearSocio(socio3);


        servicioClub.crearActividad(direccion, actividad);

        //Comprobamos que solo se asignen plazas si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(InscripcionAbierta.class);


        actividad.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad);

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(SolicitudNoExistente.class);

        actividad.setFechaInicioInscripcion(LocalDate.now());
        servicioClub.modificarFechaActividad(actividad);
        servicioClub.marcarCuotaPagada(direccion, socio);
        servicioClub.crearSolicitud(socio, actividad, 1);

        //Comprobamos que se haya asignado la plaza.

        Actividad actividad1 = servicioClub.buscarActividadesAbiertas().getFirst();

        actividad1.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad1);
        servicioClub.asignarPlaza(direccion, socio, actividad1);
        int plazasAceptadas = actividad1.buscarSolicitudPorEmail(socio.getEmail()).get().getPlazasAceptadas();
        assertEquals(1, plazasAceptadas);

        //Comprobamos que no se asignen más plazas de las solicitadas.
        servicioClub.asignarPlaza(direccion, socio, actividad1);
        actividad1.setFechaInicioInscripcion(LocalDate.now());
        servicioClub.modificarFechaActividad(actividad1);
        actividad1 = servicioClub.buscarActividadesAbiertas().getFirst();
        actividad1.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad1);
        assertEquals(plazasAceptadas, actividad1.buscarSolicitudPorEmail(socio.getEmail()).get().getPlazasAceptadas());

        //Comprobamos que no se asignen más plazas de las disponibles.
        servicioClub.marcarCuotaPagada(direccion, socio2);
        servicioClub.marcarCuotaPagada(direccion, socio3);

        actividad1.setFechaInicioInscripcion(LocalDate.now());
        servicioClub.modificarFechaActividad(actividad1);
        servicioClub.crearSolicitud(socio2, actividad1, 1);
        servicioClub.crearSolicitud(socio3, actividad1, 1);
        actividad1.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad1);

        servicioClub.asignarPlaza(direccion, socio2, actividad1);
        servicioClub.asignarPlaza(direccion, socio3, actividad1);

        actividad1.setFechaInicioInscripcion(LocalDate.now());
        servicioClub.modificarFechaActividad(actividad1);
        actividad1 = servicioClub.buscarActividadesAbiertas().getFirst();
        assertEquals(actividad1.getPlazas(), actividad1.getPlazasOcupadas());
    }

    @Test
    @DirtiesContext
    void testBuscarSolcitudPorId() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.crearSocio(socio);
        servicioClub.crearActividad(direccion, actividad);
        servicioClub.marcarCuotaPagada(direccion, socio);
        servicioClub.crearSolicitud(socio, actividad, 2);

        Solicitud solicitudNoExistente = new Solicitud(socio, 2);

        //Comprobamos que no devuelva la solicitud si no existe.
        assertEquals(Optional.empty(), servicioClub.buscarSolicitudPorId(direccion, actividad, solicitudNoExistente.getId()));

        //Comprobamos que devuelva la solicitud si existe.
        Solicitud solicitud = servicioClub.buscarActividadPorId(actividad.getId()).get().buscarSolicitudPorEmail(socio.getEmail()).get();
        assertEquals(solicitud.getId(), servicioClub.buscarSolicitudPorId(direccion, actividad, solicitud.getId()).get().getId());
    }

    @Test
    @DirtiesContext
    void testQuitarPlaza() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(socio, socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        servicioClub.crearSocio(socio);
        servicioClub.crearActividad(direccion, actividad);

        //Comprobamos que solo se asignen plazas si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(direccion, socio, actividad)).isInstanceOf(InscripcionAbierta.class);

        servicioClub.marcarCuotaPagada(direccion, socio);
        servicioClub.crearSolicitud(socio, actividad, 2);

        //Cerramos el periodo de inscripción.
        actividad.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad);

        //Comprobamos que se haya quitado la plaza.
        servicioClub.asignarPlaza(direccion, socio, actividad);
        servicioClub.quitarPlaza(direccion, socio, actividad);

        Actividad actividadActualizada = servicioClub.buscarActividadPorId(actividad.getId()).get();
        Solicitud solicitud = actividadActualizada.buscarSolicitudPorEmail(socio.getEmail()).get();

        int plazasAceptadas = servicioClub.buscarSolicitudPorId(direccion, actividadActualizada, solicitud.getId()).get().getPlazasAceptadas();
        assertEquals(0, plazasAceptadas);
        assertEquals(0, actividadActualizada.getPlazasOcupadas());
    }
}
