package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.app.ClubSocios;
import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.excepciones.FechaNoValida;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest(classes = ClubSocios.class)
public class TestRepositorioActividades {

    @Autowired
    RepositorioActividades repositorioActividades;

    @Test
    @DirtiesContext
    void TestCrearActividad() {
        // Comprobamos que no se pueda crear una actividad con fecha de fin de inscripción anterior a la de inicio
        var actividad2 = new Actividad("Actividad 2", "Descripcion 2", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10));
        assertThatThrownBy(() -> repositorioActividades.crearActividad(actividad2)).isInstanceOf(FechaNoValida.class);

        // Comprobamos que no se pueda crear una actividad con fecha de celebración anterior a la de fin de inscripción
        var actividad3 = new Actividad("Actividad 3", "Descripcion 3", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(5));
        assertThatThrownBy(() -> repositorioActividades.crearActividad(actividad3)).isInstanceOf(FechaNoValida.class);

        // Comprobamos que se pueda crear una actividad con fechas válidas
        var actividad4 = new Actividad("Actividad 4", "Descripcion 4", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));
        assertThatCode(() -> repositorioActividades.crearActividad(actividad4)).doesNotThrowAnyException();
    }


    @Test
    @DirtiesContext
    void TestBuscaTodasActividadesAbiertas() {
        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades abiertas
        assertThatThrownBy(() -> repositorioActividades.buscaTodasActividadesAbiertas()).isInstanceOf(NoHayActividades.class);

        // Comprobamos que se devuelva una lista con todas las actividades abiertas
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now(), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10));
        var actividad2 = new Actividad("Actividad 2", "Descripcion 2", 10, 10,
                LocalDate.now(), LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(7));
        repositorioActividades.crearActividad(actividad1);
        repositorioActividades.crearActividad(actividad2);
        assertThat(repositorioActividades.buscaTodasActividadesAbiertas()).isNotEmpty();

        // Comprobamos que no se devuelvan actividades cerradas
        var actividadCerrada = new Actividad("ActividadCerrada", "Descripcion Cerrada", 10, 10,
                LocalDate.now(), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3));
        repositorioActividades.crearActividad(actividadCerrada);
        actividadCerrada.setFechaInicioInscripcion(LocalDate.now().minusDays(5))
                .setFechaFinInscripcion(LocalDate.now().minusDays(5))
                .setFechaCelebracion(LocalDate.now().minusDays(5));
        actividadCerrada = repositorioActividades.actualizar(actividadCerrada);
        assertThat(repositorioActividades.buscaTodasActividadesAbiertas()).doesNotContain(actividadCerrada);
    }

    @Test
    @DirtiesContext
    void TestBuscarPorId() {

        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades
        assertEquals(Optional.empty(), repositorioActividades.buscarPorId(1));

        // Comprobamos que se devuelva la actividad con el id dado
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));
        actividad1 = repositorioActividades.crearActividad(actividad1);
        assertThat(repositorioActividades.buscarPorId(actividad1.getId())).get().isEqualTo(actividad1);
    }

}