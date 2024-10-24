package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.ActividadYaExistente;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestServicioClub {

    private ServicioClub servicioClub;

    @BeforeEach
    public void setUp() {
        // Crea una instancia de ServicioClub antes de cada test
        servicioClub = new ServicioClub();
        Socio socio = new Socio("Socio", "Prueba", "socio_prueba@club.com", "621302025", "password123");
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(7));

        servicioClub.crearActividad(actividad);

        // Simular la inyección de dependencias
        servicioClub.anadirSocio(socio);  // Asumiendo que puedes modificar directamente por simplicidad
    }

    @Test
    public void testLogin() {
        // Verifica el login con el administrador
        assertEquals("admin@club.es", servicioClub.login("admin@club.es", "ElAdMiN").get().getEmail());

        // Verifica el login con un socio válido
        assertEquals("socio_prueba@club.com", servicioClub.login("socio_prueba@club.com", "password123").get().getEmail());

        // Comprobamos valores nulos.
        assertEquals(Optional.empty(), servicioClub.login("", "password123"));
        assertEquals(Optional.empty(), servicioClub.login("socio_prueba@club.com", ""));
        assertEquals(Optional.empty(), servicioClub.login("", ""));

        // Verifica el login con credenciales incorrectas
        assertEquals(Optional.empty(), servicioClub.login("socio@club.com", "wrongpassword"));

        // Verifica el login con un email no registrado
        assertEquals(Optional.empty(), servicioClub.login("noexiste@club.com", "password123"));
    }

    @Test
    void testAniadirSocio() {
        //verificamos que no se pueda añadir un socio igual al admin.
        Socio admin = servicioClub.login("admin@club.es", "ElAdMiN").get();
        assertThrows(SocioYaRegistrado.class, () -> servicioClub.anadirSocio(admin));

        //verificamos que no se pueda añadir un socio igual al otro usuario ya registrado.
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123").get();
        assertThrows(SocioYaRegistrado.class, () -> servicioClub.anadirSocio(socio));

        //verificamos que se pueda añadir un socio no registrado.
        Socio socioNoRegistrado = new Socio("Socio", "-", "socio_no_registrado@club.com", "+34 123456789", "password123");
        assertDoesNotThrow(() -> servicioClub.anadirSocio(socioNoRegistrado));
    }

    @Test
    @DirtiesContext
    void testAnadirActividad() {
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(7));
        assertThatThrownBy(() -> servicioClub.crearActividad(actividad)).isInstanceOf(ActividadYaExistente.class);
    }

}
