package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.app.ClubSocios;
import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.excepciones.FechaNoValida;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

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
    void buscaTodasActividadesAbiertas() {
        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades abiertas
        assertThatThrownBy(() -> repositorioActividades.buscaTodasActividadesAbiertas()).isInstanceOf(NoHayActividades.class);

        // Comprobamos que se devuelva una lista con todas las actividades abiertas
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10));
        repositorioActividades.crearActividad(actividad1);
        assertThat(repositorioActividades.buscaTodasActividadesAbiertas()).isNotEmpty();

        // Comprobamos que no se devuelvan actividades cerradas
        var actividad2 = new Actividad("Actividad 2", "Descripcion 2", 10, 10,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1));
        repositorioActividades.crearActividad(actividad2);
        assertThat(repositorioActividades.buscaTodasActividadesAbiertas()).doesNotContain(actividad2);
    }

    @Test
    void buscarPorId() {

        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades
        assertThatThrownBy(() -> repositorioActividades.buscarPorId(1)).isInstanceOf(NoHayActividades.class);

        // Comprobamos que se devuelva la actividad con el id dado
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));
        repositorioActividades.crearActividad(actividad1);
        assertThat(repositorioActividades.buscarPorId(actividad1.getId())).isEqualTo(actividad1);
    }
}