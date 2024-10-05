package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(Optional.empty(), servicioClub.login("","password123"));
        assertEquals(Optional.empty(), servicioClub.login("socio_prueba@club.com",""));
        assertEquals(Optional.empty(), servicioClub.login("",""));

        // Verifica el login con credenciales incorrectas
        assertEquals(Optional.empty(), servicioClub.login("socio@club.com", "wrongpassword"));

        // Verifica el login con un email no registrado
        assertEquals(Optional.empty(), servicioClub.login("noexiste@club.com", "password123"));
    }

    @Test
    void testAniadirSocio() {
    }
}
