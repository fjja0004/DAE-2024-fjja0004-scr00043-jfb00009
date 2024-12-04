package es.ujaen.dae.clubsocios.rest;

import es.ujaen.dae.clubsocios.entidades.Socio;
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
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = es.ujaen.dae.clubsocios.app.ClubSocios.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles={"test"})
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
    public void testNuevoSocio(){

        //Socio invalido
        DTOSocio socio = new DTOSocio( "nombre", "apellidos", "email", "tlf", "clave");
        var respuesta= restTemplate.postForEntity("/socios",socio, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        //Socio Valido
        DTOSocio socio2=new DTOSocio("Socio", "Prueba", "socio_prueba@club.com", "621302025", "password123");
        respuesta= restTemplate.postForEntity("/socios",socio2, Void.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Socio Ya Creado
        respuesta= restTemplate.postForEntity("/socios",socio2,Void.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void testLoginSocio() {
    }
}
