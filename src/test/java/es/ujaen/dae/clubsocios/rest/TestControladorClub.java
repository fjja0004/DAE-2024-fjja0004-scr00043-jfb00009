package es.ujaen.dae.clubsocios.rest;

import es.ujaen.dae.clubsocios.rest.dto.DTOActividad;
import es.ujaen.dae.clubsocios.rest.dto.DTOSocio;
import es.ujaen.dae.clubsocios.rest.dto.DTOSolicitud;
import es.ujaen.dae.clubsocios.rest.dto.DTOTemporada;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = es.ujaen.dae.clubsocios.app.ClubSocios.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestControladorClub {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    /*
     * Crear el RestTemplateBuilder para poder hacer las peticiones al servidor
     */
    @PostConstruct
    void crearRestTemplateBuilder() {
        var restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/club");

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    @DirtiesContext
    public void testNuevoSocio() {
        //Socio no válido
        DTOSocio socio = new DTOSocio("nombre", "apellidos", "email", "tlf", "clave");
        var respuesta = restTemplate.postForEntity("/socios", socio, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        //Socio válido
        DTOSocio socio2 = new DTOSocio("Socio", "Prueba", "socio@club.com", "612345678", "password123");
        respuesta = restTemplate.postForEntity("/socios", socio2, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Socio ya creado
        respuesta = restTemplate.postForEntity("/socios", socio2, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void testLoginSocio() {
        //Creo el socio con el que voy a hacer login
        DTOSocio socio = new DTOSocio("Socio", "Prueba", "socio@club.com", "621302025", "password123");
        var respuesta = restTemplate.postForEntity("/socios", socio, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Login con email incorrecto
        var respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class, "error@gmail.com", "password123");
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        //Login con contraseña incorrecta
        respuestaLogin = restTemplate.withBasicAuth(socio.email(), "Error").getForEntity("/socios/{email}",
                DTOSocio.class, socio.email());

        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        //Login correcto
        respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class,
                socio.email(), socio.clave());
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaLogin.getBody().email()).isEqualTo(socio.email());
    }

    @Test
    @DirtiesContext
    void testNuevaTemporada() {
        var temporada = new DTOTemporada(LocalDate.now().getYear() + 1);

        //Login como administador
        var respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class,
                "admin@club.com", "admin");
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Creación de una nueva temporada
        var respuesta = restTemplate.postForEntity("/temporadas", temporada, DTOTemporada.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //prueba con socio erroneo
        DTOSocio socio=new DTOSocio("socio","apellidos", "email@gmail.com", "tlf", "clave");
        var respuesta1 = restTemplate.withBasicAuth(socio.email(), socio.clave()).postForEntity("/temporadas", null, Void.class);
        assertThat(respuesta1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testObtenerTemporadas() {
        var temporada1 = new DTOTemporada(LocalDate.now().getYear() + 1);
        var temporada2 = new DTOTemporada(LocalDate.now().getYear() + 2);

        var respuesta = restTemplate.postForEntity("/temporadas", temporada1, DTOTemporada.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        respuesta = restTemplate.postForEntity("/temporadas", temporada2, DTOTemporada.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var respuestaConsulta = restTemplate.getForEntity("/temporadas", DTOTemporada[].class);
        assertThat(respuestaConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaConsulta.getBody()).hasSize(3);
        assertThat(respuestaConsulta.getBody()[0].anio()).isEqualTo(LocalDate.now().getYear());
    }

    @Test
    @DirtiesContext
    void testNuevaActividad() {
        var actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));

        var respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class,
                "admin@club.com", "admin");
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        var respuesta = restTemplate.postForEntity("/actividades", actividad, DTOActividad.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testBuscarActividadesPorTemporada() {
        var actividad1 = new DTOActividad(0, "Primera actividad", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));

        var actividad2 = new DTOActividad(0, "Segunda actividad", "Actividad de prueba", 20,
                20, 0, LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));

        //Creación de actividades
        var respuesta = restTemplate.postForEntity("/actividades", actividad1, DTOActividad.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        respuesta = restTemplate.postForEntity("/actividades", actividad2, DTOActividad.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Consulta de actividades. La lista de actividades debe tener tamaño igual a 2
        var respuestaConsulta = restTemplate.getForEntity(
                "/actividades?anio={anio}",
                DTOActividad[].class,
                LocalDate.now().getYear());
        assertThat(respuestaConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaConsulta.getBody()).hasSize(2);
        assertThat(respuestaConsulta.getBody()[0].id()).isEqualTo(1);

    }

    @Test
    @DirtiesContext
    void testNuevaSolictud() {
        var actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));
        var respuestaActividad = restTemplate.postForEntity(
                "/actividades",
                actividad,
                DTOActividad.class);
        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var socio = new DTOSocio("Socio", "Prueba", "socio@club.com", "621302025", "password123");
        var respuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class);
        assertThat(respuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actividadGuardada = restTemplate.getForEntity(
                "/actividades?anio={anio}",
                DTOActividad[].class,
                LocalDate.now().getYear()
        ).getBody()[0];

        var solicitud = new DTOSolicitud(0, 3, LocalDate.now(), 0, socio.email());
        var respuestaSolicitud = restTemplate.postForEntity(
                "/actividades/{id}/solicitudes",
                solicitud,
                DTOSolicitud.class,
                actividadGuardada.id());

        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testModificarAcompanantes() {
        var actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));
        var respuestaActividad = restTemplate.postForEntity(
                "/actividades",
                actividad,
                DTOActividad.class);
        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var socio = new DTOSocio("Socio", "Prueba", "socio@club.com", "621302025", "password123");
        var respuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class);
        assertThat(respuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actividadGuardada = restTemplate.getForEntity(
                "/actividades?anio={anio}",
                DTOActividad[].class,
                LocalDate.now().getYear()
        ).getBody()[0];

        var solicitud = new DTOSolicitud(0, 3, LocalDate.now(), 0, socio.email());
        var respuestaSolicitud = restTemplate.postForEntity(
                "/actividades/{id}/solicitudes",
                solicitud,
                DTOSolicitud.class,
                actividadGuardada.id());
        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var solicitudGuardada = respuestaSolicitud.getBody();
        var solicitudModificada = new DTOSolicitud(
                solicitudGuardada.id(),
                5,
                solicitudGuardada.fecha(),
                solicitudGuardada.plazasAceptadas(),
                solicitudGuardada.emailSocio());
        var respuestaModificacion = restTemplate.exchange(
                "/actividades/{id}/solicitudes/{idSolicitud}",
                HttpMethod.PUT,
                new HttpEntity<>(solicitudModificada),
                DTOSolicitud.class,
                actividadGuardada.id(),
                solicitudGuardada.id());

        assertThat(respuestaModificacion.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaModificacion.getBody().nAcompanantes()).isEqualTo(5);
    }

    @Test
    @DirtiesContext
    void testEliminarSolicitud() {
        var actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));
        var respuestaActividad = restTemplate.postForEntity(
                "/actividades",
                actividad,
                DTOActividad.class);
        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var socio = new DTOSocio("Socio", "Prueba", "socio@club.com", "621302025", "password123");
        var respuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class);
        assertThat(respuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actividadGuardada = restTemplate.getForEntity(
                "/actividades?anio={anio}",
                DTOActividad[].class,
                LocalDate.now().getYear()
        ).getBody()[0];

        var solicitud = new DTOSolicitud(0, 3, LocalDate.now(), 0, socio.email());
        var respuestaSolicitud = restTemplate.postForEntity(
                "/actividades/{id}/solicitudes",
                solicitud,
                DTOSolicitud.class,
                actividadGuardada.id());
        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var solicitudGuardada = respuestaSolicitud.getBody();
        var respuestaEliminacion = restTemplate.exchange(
                "/actividades/{id}/solicitudes/{idSolicitud}",
                HttpMethod.DELETE,
                null,
                Void.class,
                actividadGuardada.id(),
                solicitudGuardada.id());

        assertThat(respuestaEliminacion.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void testObtenerSolicitudesActividad() {
        //Creación de la actividad
        var actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));
        var respuestaActividad = restTemplate.postForEntity(
                "/actividades",
                actividad,
                DTOActividad.class);
        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Creación de los socios
        var socio = new DTOSocio("Primer socio", "Prueba", "socio@club.com", "621302025", "password123");
        var socio2 = new DTOSocio("Segundo socio", "Prueba", "socio2@club.com", "621302025", "password123");
        var respuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class);
        assertThat(respuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        respuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio2,
                Void.class);
        assertThat(respuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Obtención de la actividad guardada
        var actividadGuardada = restTemplate.getForEntity(
                "/actividades?anio={anio}",
                DTOActividad[].class,
                LocalDate.now().getYear()
        ).getBody()[0];

        //Creación de las solicitudes
        var solicitud1 = new DTOSolicitud(0, 3, LocalDate.now(), 0, socio.email());
        var respuestaSolicitud = restTemplate.postForEntity(
                "/actividades/{id}/solicitudes",
                solicitud1,
                DTOSolicitud.class,
                actividadGuardada.id());
        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var solicitud2 = new DTOSolicitud(0, 4, LocalDate.now(), 0, socio2.email());
        respuestaSolicitud = restTemplate.postForEntity(
                "/actividades/{id}/solicitudes",
                solicitud2,
                DTOSolicitud.class,
                actividadGuardada.id());
        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Obtención de las solicitudes de la actividad
        var respuestaConsulta = restTemplate.getForEntity(
                "/actividades/{id}/solicitudes",
                DTOSolicitud[].class,
                actividadGuardada.id());
        assertThat(respuestaConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaConsulta.getBody()).hasSize(2);
    }
}

