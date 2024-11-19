package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.app.ClubSocios;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = ClubSocios.class)
public class TestRepositorioSocios {
    @Autowired
    private RepositorioSocios repositorioSocios;

    @Test
    @DirtiesContext
    @Transactional
    public void testOperacionesCRUD() {
        Socio socio = new Socio("nombre", "apellidos", "email@gmail.com", "123456789", "clave");
        Socio socioSinRegistrar = new Socio("nombre", "apellidos", "email2@gmail.com", "123456789", "clave");

        //Se añade un socio al repositorio
        assertDoesNotThrow(() -> repositorioSocios.guardar(socio));

        //Se comprueba que el socio se ha añadido y que las operaciones de búsqueda funcionan correctamente
        assertThat(repositorioSocios.buscar(socio.getEmail())).isEqualTo(Optional.of(socio));

        //Se comprueba que no se puede añadir el mismo socio más de una vez
        assertThatThrownBy(() -> repositorioSocios.guardar(socio)).isInstanceOf(SocioYaRegistrado.class);

        //Se comprueba que no se puede buscar un socio que no está registrado
        assertEquals(Optional.empty(), repositorioSocios.buscar(socioSinRegistrar.getEmail()));

    }
}
