package es.ujaen.dae.clubsocios.entidades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestTemporada {
    
    @Test
    void testCrearActividad() {
        Temporada temporada = new Temporada(2021);
        Actividad actividad = new Actividad("Actividad 1", "Descripción de la actividad 1", 10, 20, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2), LocalDate.now().plusDays(7));
        temporada.crearActividad(actividad);
        assertTrue(temporada.buscarActividadPorTitulo("Actividad 1"));
    }

    @Test
    void testBuscarActividadPorTitulo() {
        Temporada temporada = new Temporada(2021);
        Actividad actividad = new Actividad("Actividad 1", "Descripción de la actividad 1", 10, 20, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2), LocalDate.now().plusDays(7));
        temporada.crearActividad(actividad);
        assertTrue(temporada.buscarActividadPorTitulo("Actividad 1"));
        assertFalse(temporada.buscarActividadPorTitulo("Actividad 2"));
    }

}