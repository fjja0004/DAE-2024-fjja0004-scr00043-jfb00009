package es.ujaen.dae.clubsocios.rest;

import es.ujaen.dae.clubsocios.rest.dto.DTOActividad;
import es.ujaen.dae.clubsocios.rest.dto.DTOSocio;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
        respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class, socio.email(), "1111");
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
    void testNuevaActividad() {
        //TODO: revisar con qué id se guarda la actividad
        //creo actividad correcta
        DTOActividad actividad = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now().plusDays(2), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));

        var respuesta = restTemplate.postForEntity("/actividades", actividad, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //creo actividad invalida
        DTOActividad actividad2 = new DTOActividad(0, "Actividad de prueba", "Actividad de prueba", -10,
                -10, 0, LocalDate.now(), LocalDate.now(), LocalDate.now());

        respuesta = restTemplate.postForEntity("/actividades", actividad2, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @Test
    @DirtiesContext
    void testBuscarActividadesPorTemporada() {

        //hace login del socio direccion
        var respuestaLogin = restTemplate.getForEntity("/socios/{email}?clave={clave}",
                DTOSocio.class,
                "admin@club.com", "admin");
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        //creo actividades abiertas
        DTOActividad actividad = new DTOActividad(1, "Actividad de prueba", "Actividad de prueba", 10,
                10, 0, LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(10));
        DTOActividad actividad1 = new DTOActividad(2, "Actividad de prueba1", "Actividad de prueba1", 100,
                100, 0, LocalDate.now(), LocalDate.now().plusDays(8), LocalDate.now().plusDays(11));

        var respuesta = restTemplate.postForEntity("/actividades", actividad, DTOActividad.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        respuesta = restTemplate.postForEntity("/actividades", actividad1, DTOActividad.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        var respuestaConsulta = restTemplate.getForEntity("/actividades?anio={anio}", DTOActividad[].class, LocalDate.now().getYear());
        assertThat(respuestaConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(respuestaConsulta.getBody()).hasSize(2);
        assertThat(respuestaConsulta.getBody()[0].id()).isEqualTo(1);
    }
}
