package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.IntentoBorrarAdmin;
import es.ujaen.dae.clubsocios.excepciones.SocioNoRegistrado;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestServicioClub {

    private ServicioClub servicioClub;

    @BeforeEach
    public void setUp() {
        // Crea una instancia de ServicioClub antes de cada test
        servicioClub = new ServicioClub();
        Socio socio = new Socio("Socio", "Prueba", "socio_prueba@club.com", "123456789", "password123");

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
    void testBorrarSocio() {
        //verificamos que no se pueda borrar un socio no registrado.
        Socio socioNoRegistrado = new Socio("Socio", "-", "socio_no_registrado@club.com", "+34 123456789", "password123");
        assertThatThrownBy(() -> servicioClub.borrarSocio(socioNoRegistrado)).isInstanceOf(SocioNoRegistrado.class);

        //verificamos que no se pueda borrar un socio igual al admin.
        Socio admin = servicioClub.login("admin@club.es", "ElAdMiN").get();
        assertThatThrownBy(() -> servicioClub.borrarSocio(admin)).isInstanceOf(IntentoBorrarAdmin.class);

        //verificamos que se pueda borrar un socio registrado
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123").get();
        assertDoesNotThrow(() -> servicioClub.borrarSocio(socio));
    }

}
