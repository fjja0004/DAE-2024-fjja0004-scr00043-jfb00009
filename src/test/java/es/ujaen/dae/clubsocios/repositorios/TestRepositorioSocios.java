package es.ujaen.dae.clubsocios.repositorios;

import es.ujaen.dae.clubsocios.app.ClubSocios;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = ClubSocios.class)
public class TestRepositorioSocios {
    @Autowired
    private RepositorioSocios repositorioSocios;

    @Test
    @DirtiesContext
    public void testOperacionesCRUD() {
        Socio socio = new Socio("nombre", "apellidos", "email@gmail.com", "623456789", "clave");
        Socio socioSinRegistrar = new Socio("nombre", "apellidos", "email2@gmail.com", "623456789", "clave");

        //Se añade un socio al repositorio
        assertDoesNotThrow(() -> repositorioSocios.guardar(socio));

        //Se comprueba que el socio se ha añadido y que las operaciones de búsqueda funcionan correctamente
        assertThat(repositorioSocios.buscar(socio.getEmail())).isEqualTo(Optional.of(socio));

        //Se comprueba que no se puede añadir el mismo socio más de una vez
        assertThatThrownBy(() -> repositorioSocios.guardar(socio)).isInstanceOf(SocioYaRegistrado.class);

        //Se comprueba que no se puede buscar un socio que no está registrado
        assertEquals(Optional.empty(), repositorioSocios.buscar(socioSinRegistrar.getEmail()));

    }

    @Test
    @DirtiesContext
    public void testCuotas() {
        Socio socio1 = new Socio("nombre", "apellidos", "email1@gmail.com", "623456789", "clave");
        Socio socio2 = new Socio("nombre", "apellidos", "email2@gmail.com", "623456789", "clave");
        Socio socio3 = new Socio("nombre", "apellidos", "email3@gmail.com", "623456789", "clave");
        Socio socio4 = new Socio("nombre", "apellidos", "email4@gmail.com", "623456789", "clave");
        repositorioSocios.guardar(socio1);
        repositorioSocios.guardar(socio2);
        repositorioSocios.guardar(socio3);
        repositorioSocios.guardar(socio4);

        //Comprobar que se se cambia la cuota pagada de false a true;
        assertFalse(repositorioSocios.buscar(socio1.getEmail()).get().isCuotaPagada());
        repositorioSocios.marcarCuotaPagada(socio1);
        assertTrue(repositorioSocios.buscar(socio1.getEmail()).get().isCuotaPagada());

        repositorioSocios.marcarCuotaPagada(socio2);
        assertTrue(repositorioSocios.buscar(socio2.getEmail()).get().isCuotaPagada());
        repositorioSocios.marcarCuotaPagada(socio3);
        assertTrue(repositorioSocios.buscar(socio3.getEmail()).get().isCuotaPagada());
        repositorioSocios.marcarCuotaPagada(socio4);
        assertTrue(repositorioSocios.buscar(socio4.getEmail()).get().isCuotaPagada());

        //comprobamos que ponga a false todas las cuotas pagadas al usar la función marcarTodasCuotasNoPagadas
        repositorioSocios.marcarTodasCuotasNoPagadas();
        assertFalse(repositorioSocios.buscar(socio1.getEmail()).get().isCuotaPagada());
        assertFalse(repositorioSocios.buscar(socio2.getEmail()).get().isCuotaPagada());
        assertFalse(repositorioSocios.buscar(socio3.getEmail()).get().isCuotaPagada());
        assertFalse(repositorioSocios.buscar(socio4.getEmail()).get().isCuotaPagada());

    }
}
