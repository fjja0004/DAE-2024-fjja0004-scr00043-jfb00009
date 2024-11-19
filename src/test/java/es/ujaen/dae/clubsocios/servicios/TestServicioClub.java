package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
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
        servicioClub.anadirSocio(new Socio("Socio", "Prueba", "socio_prueba@club.com", "621302025", "password123"));
    }

    @Test
    @DirtiesContext
    public void testLogin() {
        // Verifica el login con el administrador.
        assertEquals("admin@club.es", servicioClub.login("admin@club.es", "ElAdMiN").getEmail());

        // Verifica el login con el administrador pero con la contraseña erronea.
        assertThatThrownBy(() -> servicioClub.login("admin@club.es", "wrongpassword")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con un socio válido
        assertEquals("socio_prueba@club.com", servicioClub.login("socio_prueba@club.com", "password123").getEmail());

        // Comprobamos valores nulos para email.
        assertThatThrownBy(() -> servicioClub.login("", "password123")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con credenciales incorrectas.
        assertThatThrownBy(() -> servicioClub.login("socio_prueba@club.com", "wrongpassword")).isInstanceOf(SocioNoValido.class);

        // Verifica el login con un email no registrado.
        assertThatThrownBy(() -> servicioClub.login("noexiste@club.com", "password123")).isInstanceOf(SocioNoValido.class);
    }

    @Test
    @DirtiesContext
    public void testEsAdmin() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio noDireccion = new Socio("noadministrador", "-", "noadmin@club.es", "000000000", "NoElAdMiN");
        //combinaciones correctas.
        assertTrue(servicioClub.esAdmin(direccion));

        //combinaciones incorrectas.
        assertFalse(servicioClub.esAdmin(noDireccion));
        assertFalse(servicioClub.esAdmin(noDireccion));
        assertFalse(servicioClub.esAdmin(noDireccion));
    }

    @Test
    @DirtiesContext
    void testAnadirSocio() {
        //verificamos que no se pueda añadir un socio igual al admin.
        Socio admin = new Socio("administrador", "-", "admin@club.es", "623456789", "ElAdMiN");
        assertThatThrownBy(() -> servicioClub.anadirSocio(admin)).isInstanceOf(SocioYaRegistrado.class);

        //verificamos que no se pueda añadir un socio igual al otro usuario ya registrado.
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");
        assertThrows(SocioYaRegistrado.class, () -> servicioClub.anadirSocio(socio));

        //verificamos que se pueda añadir un socio no registrado.
        Socio socioNoRegistrado = new Socio("Socio", "-", "socio_no_registrado@club.com", "623456789", "password123");
        assertDoesNotThrow(() -> servicioClub.anadirSocio(socioNoRegistrado));
    }

    @Test
    @DirtiesContext
    void testBuscarTodosSocios() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        servicioClub.anadirSocio(socio);

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.buscarTodosSocios(socio)).isInstanceOf(OperacionDeDireccion.class);

        //Verificamos que se devuelvan todos los socios.
        assertEquals(2, servicioClub.buscarTodosSocios(direccion).size());
    }

    @Test
    @DirtiesContext
    void testCrearActividad() {
        //Actividad válida.
        Actividad actividad2 = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        Actividad actividadMalHecha = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(7), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10));

        Socio direccion = servicioClub.login("admin@club.es", "ElAdMiN"),
                noDireccion = servicioClub.login("socio_prueba@club.com", "password123");

        //Comprobaciones para actividades no válidas.
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);
        actividadMalHecha.setFechaInicioInscripcion(LocalDate.now().plusDays(1))
                .setFechaCelebracion(LocalDate.now()).setFechaFinInscripcion(LocalDate.now().plusDays(10));
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);
        actividadMalHecha.setFechaCelebracion(LocalDate.now().plusDays(7));
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);

        /*Comprobamos que el usuario que quiera añadir una actividad válida tenga permisos de administrador.*/
        assertThatThrownBy(() -> servicioClub.crearActividad(noDireccion, actividad2)).isInstanceOf(OperacionDeDireccion.class);

        //Se añade correctamente la actividad.
        assertDoesNotThrow(() -> servicioClub.crearActividad(direccion, actividad2));

        /*Comprobamos que el usuario que quiera añadir una actividad no válida tenga permisos de administrador.*/
        assertThatThrownBy(() -> servicioClub.crearActividad(noDireccion, actividad2)).isInstanceOf(OperacionDeDireccion.class);

        //No acepta actividades repetidas.
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividad2)).isInstanceOf(ActividadYaExistente.class);
    }

    @Test
    @DirtiesContext
    void testMarcarCuotaPagada() {
        Socio direccion = servicioClub.login("admin@club.es", "ElAdMiN");
        Socio socioTest = servicioClub.login("socio_prueba@club.com", "password123");

        //Compruebo que se hace como administrador.
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(socioTest, direccion)).isInstanceOf(OperacionDeDireccion.class);

        //compruebo que funcione con los datos correctos.
        assertDoesNotThrow(() -> servicioClub.marcarCuotaPagada(direccion, socioTest));

        //Comprobar que se ha marcado la cuota como pagada.
        assertTrue(socioTest.isCuotaPagada());

        //Compruebo que el socio no tuviera ya pagada la cuota.
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(direccion, socioTest)).isInstanceOf(PagoYaRealizado.class);

        //Compruebo que el socio exista en el sistema (en nuestro caso el administrador no está en memoria con el resto de socios).
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(direccion, direccion)).isInstanceOf(SocioNoValido.class);

    }

    @Test
    @DirtiesContext
    void buscarActividadesAbiertas() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10));

        //Comprobamos que la lista esté vacía si no hay actividades abiertas.

        //Si no hay ninguna.
        assertEquals(0, servicioClub.buscarActividadesAbiertas().size());

        //Si hay alguna, pero todas están cerradas.
        servicioClub.crearActividad(direccion, actividadCerrada);
        assertEquals(0, servicioClub.buscarActividadesAbiertas().size());

        //Si hay tanto abiertas como cerradas, solo se devuelven las abiertas.
        servicioClub.crearActividad(direccion, actividadAbierta);
        assertEquals(1, servicioClub.buscarActividadesAbiertas().size());
        assertTrue(servicioClub.buscarActividadesAbiertas().contains(actividadAbierta));

    }

    @Test
    @DirtiesContext
    void buscarTodasActividadesTemporadaActual() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10));

        //Comprobamos que la lista esté vacía si no hay actividades.
        assertEquals(0, servicioClub.buscarTodasActividadesTemporadaActual().size());

        //Comprobamos que se devuelvan las actividades de la temporada actual.
        servicioClub.crearActividad(direccion, actividadAbierta);
        servicioClub.crearActividad(direccion, actividadCerrada);
        assertTrue(servicioClub.buscarTodasActividadesTemporadaActual().contains(actividadAbierta));
        assertTrue(servicioClub.buscarTodasActividadesTemporadaActual().contains(actividadCerrada));

        //TODO: Comprobar que no se devuelven actividades de temporadas anteriores.
    }

    @Test
    @DirtiesContext
    void testRealizarSolicitud() {

        Socio direccion = new Socio("administrador", "-", "admin@club.es", "621302025", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(direccion, actividadCerrada);

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.realizarSolicitud(socio, actividadCerrada, 3)).isInstanceOf(SocioNoValido.class);

        servicioClub.anadirSocio(socio);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.realizarSolicitud(socio, actividadAbierta, 3)).isInstanceOf(NoHayActividades.class);

        //Comprobamos que se lance una excepción si la actividad no está abierta.
        assertThatThrownBy(() -> servicioClub.realizarSolicitud(socio, actividadCerrada, 3)).isInstanceOf(SolicitudNoValida.class);

        servicioClub.crearActividad(direccion, actividadAbierta);

        //Comprobamos que no se lance una excepción si la solicitud es correcta.
        assertDoesNotThrow(() -> servicioClub.realizarSolicitud(socio, actividadAbierta, 3));

        //Comprobamos que se lance una excepción si la solicitud ya se ha realizado.
        assertThatThrownBy(() -> servicioClub.realizarSolicitud(socio, actividadAbierta, 3)).isInstanceOf(SolicitudYaRealizada.class);

    }

    @Test
    @DirtiesContext
    void testModificarAcompanantes() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "621302025", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad a la que no es posible inscribirse.
        Actividad actividadCerrada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(direccion, actividadCerrada);

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.modificarAcompanantes(socio, actividadCerrada, 3)).isInstanceOf(SocioNoValido.class);

        servicioClub.anadirSocio(socio);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.modificarAcompanantes(socio, actividadAbierta, 3)).isInstanceOf(NoHayActividades.class);

        //Comprobamos que se lance una excepción si la actividad no está abierta.
        actividadCerrada.setFechaFinInscripcion(LocalDate.now().plusDays(1));
        servicioClub.realizarSolicitud(socio, actividadCerrada, 3);
        actividadCerrada.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> servicioClub.modificarAcompanantes(socio, actividadCerrada, 3)).isInstanceOf(InscripcionCerrada.class);

        servicioClub.crearActividad(direccion, actividadAbierta);

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.modificarAcompanantes(socio, actividadAbierta, 3)).isInstanceOf(SolicitudNoExistente.class);

        //Comprobamos que no se lance una excepción si la modificación es correcta.
        servicioClub.realizarSolicitud(socio, actividadAbierta, 3);
        assertDoesNotThrow(() -> servicioClub.modificarAcompanantes(socio, actividadAbierta, 5));

        //Comprobamos que se haya modificado el número de acompañantes.
        assertEquals(5, actividadAbierta.buscarSolicitudPorEmail(socio.getEmail()).get().getnAcompanantes());

    }

    @Test
    @DirtiesContext
    void buscarSolicitudesDeActividad() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
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
        servicioClub.anadirSocio(socio);
        servicioClub.realizarSolicitud(socio, actividad, 3);
        assertEquals(1, servicioClub.buscarSolicitudesDeActividad(direccion, actividad).size());

    }

    @Test
    @DirtiesContext
    void cancelarSolicitud() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(SocioNoValido.class);

        servicioClub.anadirSocio(socio);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(NoHayActividades.class);

        servicioClub.crearActividad(direccion, actividad);

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(SolicitudNoExistente.class);
        servicioClub.realizarSolicitud(socio, actividad, 3);

        //Comprobamos que no permita cancelar la solicitud si ha terminado el plazo de inscripción.
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(InscripcionCerrada.class);

        //Comprobamos que se haya cancelado la solicitud.
        actividad.setFechaFinInscripcion(LocalDate.now().plusDays(7));
        assertDoesNotThrow(() -> servicioClub.cancelarSolicitud(socio, actividad));
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(socio, actividad)).isInstanceOf(SolicitudNoExistente.class);
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
                2, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(socio, socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(SocioNoValido.class);

        servicioClub.anadirSocio(socio);
        servicioClub.anadirSocio(socio2);
        servicioClub.anadirSocio(socio3);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(NoHayActividades.class);

        servicioClub.crearActividad(direccion, actividad);

        //Comprobamos que solo se asignen plazas si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(InscripcionAbierta.class);

        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(direccion, socio, actividad)).isInstanceOf(SolicitudNoExistente.class);

        actividad.setFechaFinInscripcion(LocalDate.now().plusDays(7));
        servicioClub.realizarSolicitud(socio, actividad, 1);
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));

        //Comprobamos que se haya asignado la plaza.
        servicioClub.asignarPlaza(direccion, socio, actividad);
        int plazasAceptadas = actividad.buscarSolicitudPorEmail(socio.getEmail()).get().getPlazasAceptadas();
        assertEquals(1, plazasAceptadas);

        //Comprobamos que no se asignen más plazas de las solicitadas.
        servicioClub.asignarPlaza(direccion, socio, actividad);
        assertEquals(plazasAceptadas, actividad.buscarSolicitudPorEmail(socio.getEmail()).get().getPlazasAceptadas());

        //Comprobamos que no se asignen más plazas de las disponibles.
        actividad.setFechaFinInscripcion(LocalDate.now().plusDays(7));
        servicioClub.realizarSolicitud(socio2, actividad, 1);
        servicioClub.realizarSolicitud(socio3, actividad, 1);
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        servicioClub.asignarPlaza(direccion, socio2, actividad);
        servicioClub.asignarPlaza(direccion, socio3, actividad);
        assertEquals(actividad.getPlazas(), actividad.getPlazasOcupadas());
    }

    @Test
    @DirtiesContext
    void testQuitarPlaza() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio socio = new Socio("Socio", "Prueba", "socio@gmail.com", "621302025", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                2, LocalDate.now().minusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(socio, socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que se lance una excepción si el socio no está registrado.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(direccion, socio, actividad)).isInstanceOf(SocioNoValido.class);

        servicioClub.anadirSocio(socio);

        //Comprobamos que se lance una excepción si la actividad no existe.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(direccion, socio, actividad)).isInstanceOf(NoHayActividades.class);

        servicioClub.crearActividad(direccion, actividad);

        //Comprobamos que solo se asignen plazas si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(direccion, socio, actividad)).isInstanceOf(InscripcionAbierta.class);

        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(direccion, socio, actividad)).isInstanceOf(SolicitudNoExistente.class);

        actividad.setFechaFinInscripcion(LocalDate.now().plusDays(7));
        servicioClub.realizarSolicitud(socio, actividad, 2);
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));

        //Comprobamos que se haya quitado la plaza.
        servicioClub.asignarPlaza(direccion, socio, actividad);
        servicioClub.quitarPlaza(direccion, socio, actividad);
        int plazasAceptadas = actividad.buscarSolicitudPorEmail(socio.getEmail()).get().getPlazasAceptadas();
        assertEquals(0, plazasAceptadas);
        assertEquals(0, actividad.getPlazasOcupadas());
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
        //Verificamos que se lance una excepción si la temporada ya existe.
        assertThatThrownBy(() -> servicioClub.crearNuevaTemporada()).isInstanceOf(TemporadaYaExistente.class);
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
}
