package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.excepciones.FechaNoValida;
import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

@ActiveProfiles("test")
public class TestRepositorioActividades {

    RepositorioActividades repositorioActividades;

    @BeforeEach
    void setUp() {
        repositorioActividades = new RepositorioActividades();
    }

    @Test
    void crear() {
        // Comprobamos que no se pueda crear una actividad con fecha de fin de inscripción anterior a la de inicio
        var actividad2 = new Actividad("Actividad 2", "Descripcion 2", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10));
        assertThatThrownBy(() -> repositorioActividades.crear(actividad2)).isInstanceOf(FechaNoValida.class);

        // Comprobamos que no se pueda crear una actividad con fecha de celebración anterior a la de fin de inscripción
        var actividad3 = new Actividad("Actividad 3", "Descripcion 3", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(5));
        assertThatThrownBy(() -> repositorioActividades.crear(actividad3)).isInstanceOf(FechaNoValida.class);

        // Comprobamos que se pueda crear una actividad con fechas válidas
        var actividad4 = new Actividad("Actividad 4", "Descripcion 4", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));
        assertThatCode(() -> repositorioActividades.crear(actividad4)).doesNotThrowAnyException();
    }

    @Test
    void buscarTodasTemporadaActual() {
        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades en la temporada actual
        assertThatThrownBy(() -> repositorioActividades.buscarTodasTemporadaActual()).isInstanceOf(NoHayActividades.class);

        // Comprobamos que se devuelva una lista con todas las actividades de la temporada actual
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));
        repositorioActividades.crear(actividad1);
        assertThat(repositorioActividades.buscarTodasTemporadaActual()).isNotEmpty();
    }

    @Test
    void buscaTodasActividadesAbiertas() {
        // Comprobamos que se lance la excepción NoHayActividades si no hay actividades abiertas
        assertThatThrownBy(() -> repositorioActividades.buscaTodasActividadesAbiertas()).isInstanceOf(NoHayActividades.class);

        // Comprobamos que se devuelva una lista con todas las actividades abiertas
        var actividad1 = new Actividad("Actividad 1", "Descripcion 1", 10, 10,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10));
        repositorioActividades.crear(actividad1);
        assertThat(repositorioActividades.buscaTodasActividadesAbiertas()).isNotEmpty();

        // Comprobamos que no se devuelvan actividades cerradas
        var actividad2 = new Actividad("Actividad 2", "Descripcion 2", 10, 10,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1));
        repositorioActividades.crear(actividad2);
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
        repositorioActividades.crear(actividad1);
        assertThat(repositorioActividades.buscarPorId(actividad1.getId())).isEqualTo(actividad1);
    }
}