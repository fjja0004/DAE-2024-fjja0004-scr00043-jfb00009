package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoRegistrado;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
public class TestRepositorioSocios {

    @Test
    @DirtiesContext
    void testOperacionesCRUD() {
        RepositorioSocios repositorioSocios = new RepositorioSocios();
        var socio = new Socio("nombre", "apellidos", "email@gmail.com", "123456789", "clave");
        var socioSinRegistrar = new Socio("nombre", "apellidos", "email2@gmail.com", "123456789", "clave");

        //Se añade un socio al repositorio
        assertDoesNotThrow(() -> repositorioSocios.crear(socio));

        //Se comprueba que el socio se ha añadido y que las operaciones de búsqueda funcionan correctamente
        assertThat(repositorioSocios.buscarPorEmail(socio.getEmail())).isEqualTo(socio);

        //Se comprueba que no se puede añadir el mismo socio más de una vez
        assertThatThrownBy(() -> repositorioSocios.crear(socio)).isInstanceOf(SocioYaRegistrado.class);

        //Se comprueba que no se puede buscar un socio que no está registrado
        assertThatThrownBy(() -> repositorioSocios.buscarPorEmail(socioSinRegistrar.getEmail())).isInstanceOf(SocioNoRegistrado.class);

    }
}
