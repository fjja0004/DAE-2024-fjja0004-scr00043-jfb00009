package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.excepciones.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThatThrownBy(() -> servicioClub.crearSocio(admin)).isInstanceOf(SocioYaRegistrado.class);

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
        servicioClub.crearTemporadaProgramada();
        assertEquals(numeroTemporadas, servicioClub.buscarTodasTemporadas().size());

        //Verificamos que se ha creado la temporada del año actual.
        assertEquals(LocalDate.now().getYear(), servicioClub.buscarTemporadaPorAnio(LocalDate.now().getYear()).get().getAnio());

        //Verificamos que se ha creado la temporada del año siguiente.
        int anioSiguiente = LocalDate.now().getYear() + 1;
        servicioClub.crearTemporada(anioSiguiente);
        assertEquals(anioSiguiente, servicioClub.buscarTemporadaPorAnio(anioSiguiente).get().getAnio());
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
        assertThatThrownBy(() -> servicioClub.crearActividad(actividadNoValida)).isInstanceOf(FechaNoValida.class);
        assertThatThrownBy(() -> servicioClub.crearActividad(actividadNoValida2)).isInstanceOf(FechaNoValida.class);

        //Se añade correctamente la actividad.
        assertDoesNotThrow(() -> servicioClub.crearActividad(actividad));

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

        servicioClub.crearActividad(actividad);

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
        servicioClub.crearActividad(actividadCerrada);
        assertEquals(0, servicioClub.buscarActividadesAbiertas().size());

        //Comprobamos que se devuelva la actividad abierta.
        servicioClub.crearActividad(actividadAbierta);
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

        servicioClub.crearActividad(actividad);
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

        servicioClub.crearActividad(actividad);

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

        servicioClub.crearActividad(actividadCerrada);

        //Comprobamos que se lance una excepción si la actividad no está abierta.
        assertThatThrownBy(() -> servicioClub.crearSolicitud(socioSinCuota, actividadCerrada, 3)).isInstanceOf(InscripcionCerrada.class);

        servicioClub.crearActividad(actividadAbierta);

        Solicitud solicitud = servicioClub.crearSolicitud(socioSinCuota, actividadAbierta, 3);

        //Comprobamos que no aumenten las plazas ocupadas si el socio no ha pagado la cuota.
        assertEquals(0, servicioClub.buscarActividadPorId(actividadAbierta.getId()).get().getPlazasOcupadas());

        //Comprobamos que no aumenten las plazas aceptadas si el socio no ha pagado la cuota.
        assertEquals(0, solicitud.getPlazasAceptadas());

        Solicitud solicitudConCuota = servicioClub.crearSolicitud(socioConCuota, actividadAbierta, 3);

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
    void testBuscarSolicitudesDeActividad() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Comprobamos que se lance una excepción si el socio no es el administrador.
        assertThatThrownBy(() -> servicioClub.buscarSolicitudesDeActividad(socio, actividad)).isInstanceOf(OperacionDeDireccion.class);

        servicioClub.crearActividad(actividad);

        //Comprobamos que devuelva una lista vacía si no hay solicitudes.
        assertEquals(0, servicioClub.buscarSolicitudesDeActividad(admin, actividad).size());

        //Comprobamos que devuelva una lista con las solicitudes realizadas.
        servicioClub.marcarCuotaPagada(admin, socio);
        servicioClub.crearSolicitud(socio, actividad, 3);
        assertEquals(1, servicioClub.buscarSolicitudesDeActividad(admin, actividad).size());
    }

    @Test
    @DirtiesContext
    void testBuscarSolicitudPorId() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(actividad);

        //Comprobamos que devuelva la solicitud si existe.
        servicioClub.marcarCuotaPagada(admin, socio);
        Solicitud solicitud = servicioClub.crearSolicitud(socio, actividad, 3);
        assertEquals(1, servicioClub.buscarSolicitudPorId(actividad.getId(), solicitud.getId()).get().getId());
    }

    @Test
    @DirtiesContext
    void testCancelarSolicitud() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.marcarCuotaPagada(admin, socio);
        servicioClub.crearActividad(actividad);
        Solicitud solicitudExistente = servicioClub.crearSolicitud(socio, actividad, 3);
        Solicitud solicitudNoExistente = new Solicitud();

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(actividad, solicitudNoExistente)).isInstanceOf(SolicitudNoExistente.class);

        //Comprobamos que no permita cancelar la solicitud si el periodo de inscripción ha finalizado.
        actividad.setFechaInicioInscripcion(LocalDate.now().plusDays(1));
        servicioClub.modificarFechaActividad(actividad);
        assertThatThrownBy(() -> servicioClub.cancelarSolicitud(actividad, solicitudExistente)).isInstanceOf(InscripcionCerrada.class);

        actividad.setFechaInicioInscripcion(LocalDate.now());
        servicioClub.modificarFechaActividad(actividad);

        //Comprobamos que se cancele la solicitud si es válida y el periodo de inscripción está abierto.
        assertEquals(1, servicioClub.buscarActividadPorId(actividad.getId()).get().getPlazasOcupadas());
        assertDoesNotThrow(() -> servicioClub.cancelarSolicitud(actividad, solicitudExistente));
        assertEquals(0, servicioClub.buscarActividadPorId(actividad.getId()).get().getPlazasOcupadas());
    }

    @Test
    @DirtiesContext
    void testModificarSolicitud() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividadAbierta = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.marcarCuotaPagada(admin, socio);
        servicioClub.crearActividad(actividadAbierta);

        Solicitud solicitudNoExistente = new Solicitud();

        //Comprobamos que se lance una excepción si la solicitud no existe.
        assertThatThrownBy(() -> servicioClub.modificarSolicitud(actividadAbierta, solicitudNoExistente, 3)).isInstanceOf(SolicitudNoExistente.class);

        Solicitud solicitud = servicioClub.crearSolicitud(socio, actividadAbierta, 3);
        solicitud = servicioClub.modificarSolicitud(actividadAbierta, solicitud, 5);
        //Comprobamos que se haya modificado el número de acompañantes.
        assertEquals(5, solicitud.getnAcompanantes());
    }

    @Test
    @DirtiesContext
    void testAsignarPlaza() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        //Actividad para comprobar que no se asignen más plazas de las que hay disponibles.
        Actividad actividadLimitada = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                1, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.crearActividad(actividad);
        servicioClub.crearActividad(actividadLimitada);
        Solicitud solicitud = servicioClub.crearSolicitud(socio, actividad, 0);
        Solicitud solicitudLimitada = servicioClub.crearSolicitud(socio, actividadLimitada, 1);

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(socio, actividad, solicitud)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que no se asignen plazas (manualmente) si el periodo de inscripción no ha finalizado.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(admin, actividad, solicitud)).isInstanceOf(InscripcionAbierta.class);

        //Cerramos el periodo de inscripción.
        actividad.setFechaInicioInscripcion(LocalDate.now().minusDays(2));
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        servicioClub.modificarFechaActividad(actividad);

        //Comprobamos que se asigne la plaza correctamente.
        Solicitud solicitudActualizada = servicioClub.asignarPlaza(admin, actividad, solicitud);
        assertEquals(1, solicitudActualizada.getPlazasAceptadas());

        //Comprobamos que se haya actualizado el número de plazas ocupadas.
        Actividad actividadActualizada = servicioClub.buscarActividadPorId(actividad.getId()).get();
        assertEquals(1, actividadActualizada.getPlazasOcupadas());

        //Comprobamos que no se pueda asignar más plazas de las que se han solicitado.
        solicitudActualizada = servicioClub.asignarPlaza(admin, actividad, solicitudActualizada);
        //No se ha incrementado el número de plazas aceptadas.
        assertEquals(1, solicitudActualizada.getPlazasAceptadas());

        //Comprobamos que no se pueda asignar más plazas de las que hay disponibles.

        actividadLimitada.setFechaInicioInscripcion(LocalDate.now().minusDays(2));
        actividadLimitada.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        servicioClub.modificarFechaActividad(actividadLimitada);

        //Asignamos la única plaza disponible.
        servicioClub.asignarPlaza(admin, actividadLimitada, solicitudLimitada);
        //Comprobamos que no se pueda asignar más plazas.
        assertThatThrownBy(() -> servicioClub.asignarPlaza(admin, actividadLimitada, solicitudLimitada)).isInstanceOf(NoDisponibilidadPlazas.class);
    }

    @Test
    @DirtiesContext
    void testQuitarPlaza() {
        Socio admin = servicioClub.login("admin@club.com", "admin");
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");

        //Actividad a la que es posible inscribirse.
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now(), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        servicioClub.marcarCuotaPagada(admin, socio);
        servicioClub.crearActividad(actividad);
        Solicitud solicitud = servicioClub.crearSolicitud(socio, actividad, 2);

        //Comprobamos que se lance una excepción si el socio que realiza la operación no es el administrador.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(socio, actividad, solicitud)).isInstanceOf(OperacionDeDireccion.class);

        //Comprobamos que solo se asignen plazas si el periodo de inscripción ha finalizado.
        assertThatThrownBy(() -> servicioClub.quitarPlaza(admin, actividad, solicitud)).isInstanceOf(InscripcionAbierta.class);

        //Cerramos el periodo de inscripción.
        actividad.setFechaInicioInscripcion(LocalDate.now().minusDays(2));
        actividad.setFechaFinInscripcion(LocalDate.now().minusDays(1));
        servicioClub.modificarFechaActividad(actividad);

        //Comprobamos que se quite la plaza correctamente.
        assertEquals(1, solicitud.getPlazasAceptadas());
        Solicitud solicitudActualizada = servicioClub.quitarPlaza(admin, actividad, solicitud);
        assertEquals(0, solicitudActualizada.getPlazasAceptadas());
    }
}
