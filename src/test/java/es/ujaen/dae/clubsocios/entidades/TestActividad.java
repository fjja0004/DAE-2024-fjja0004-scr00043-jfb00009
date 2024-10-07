package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoDisponibilidadPlazas;
import es.ujaen.dae.clubsocios.excepciones.SolicitudNoValida;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestActividad {

    @Test
    @DirtiesContext
    void testRealizarSolicitud() {
        // Crea una instancia de una actividad
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(7));

        // Crea una instancia de un socio
        Socio socio = new Socio("Socio", "Prueba", "socio_prueba@club.com", "123456789",
                "password123");

        // Crea una instancia de una solicitud
        Solicitud solicitud = new Solicitud(2, LocalDate.now(), socio);

        // Realizar una solicitud con fecha de solicitud anterior a la fecha de inicio de inscripción
        assertThrows(SolicitudNoValida.class, () -> actividad.realizarSolicitud(solicitud));

        // Realizar una solicitud con fecha de solicitud posterior a la fecha de fin de inscripción
        solicitud.setFecha(LocalDate.now().plusDays(8));
        assertThrows(SolicitudNoValida.class, () -> actividad.realizarSolicitud(solicitud));

        // Realizar una solicitud repetida
        solicitud.setFecha(LocalDate.now().plusDays(5));
        actividad.realizarSolicitud(solicitud);
        assertThrows(SolicitudYaRealizada.class, () -> actividad.realizarSolicitud(solicitud));

        // Realizar una solicitud a una actividad con plazas agotadas
        actividad.setPlazas(1);
        Socio socio2 = new Socio("Socio2", "Prueba", "socio_prueba2@club.com",
                "621302025", "password123");
        Solicitud solicitud2 = new Solicitud(0, LocalDate.now().plusDays(5), socio2);
        assertThrows(NoDisponibilidadPlazas.class, () -> actividad.realizarSolicitud(solicitud2));
    }
}