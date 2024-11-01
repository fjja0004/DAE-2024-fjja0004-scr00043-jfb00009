package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.excepciones.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.clubsocios.app.ClubSocios.class)
@ActiveProfiles("test")
public class TestServicioClub {
    @Autowired
    private ServicioClub servicioClub;

    @BeforeEach
    public void setUp() {
        // Crea una instancia de ServicioClub antes de cada test
        servicioClub = new ServicioClub();
        //servicioClub.crearAdministrador();
        servicioClub.anadirSocio(new Socio("Socio", "Prueba", "socio_prueba@club.com", "621302025", "password123"));
        Actividad actividad = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(7));

        //servicioClub.crearActividad(actividad);

        // Simular la inyección de dependencias
        //servicioClub.anadirSocio(socio);  // Asumiendo que puedes modificar directamente por simplicidad
    }

    @Test
    public void testLogin() {
        // Verifica el login con el administrador.
        assertEquals("admin@club.es", servicioClub.login("admin@club.es", "ElAdMiN").getEmail());

        // Verifica el login con el administrador pero con la contraseña erronea.
        assertThatThrownBy(() -> servicioClub.login("admin@club.es", "wrongpassword")).isInstanceOf(ContrasenaNoValida.class);

        // Verifica el login con un socio válido
        assertEquals("socio_prueba@club.com", servicioClub.login("socio_prueba@club.com", "password123").getEmail());

        // Comprobamos valores nulos para email.
        assertThatThrownBy(() -> servicioClub.login("", "password123")).isInstanceOf(SocioNoRegistrado.class);

        // Verifica el login con credenciales incorrectas.
        assertThatThrownBy(() -> servicioClub.login("socio_prueba@club.com", "wrongpassword")).isInstanceOf(ContrasenaNoValida.class);

        // Verifica el login con un email no registrado.
        assertThatThrownBy(() -> servicioClub.login("noexiste@club.com", "password123")).isInstanceOf(SocioNoRegistrado.class);
    }

    @Test
    public void testEsAdmin() {
        Socio direccion = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        Socio noDireccion = new Socio("noadministrador", "-", "noadmin@club.es", "000000000", "NoElAdMiN");
        //combinaciones correctas.
        assertTrue(servicioClub.esAdmin(direccion));

        //combinaciones incorrectas.
        assertFalse(servicioClub.esAdmin(noDireccion));
        assertFalse(servicioClub.esAdmin(noDireccion));
        assertFalse(servicioClub.esAdmin(noDireccion));
    }

    @Test
    @DirtiesContext
    void testAniadirSocio() {
        //verificamos que no se pueda añadir un socio igual al admin.
        Socio admin = new Socio("administrador", "-", "admin@club.es", "111111111", "ElAdMiN");
        assertThatThrownBy(() -> servicioClub.anadirSocio(admin)).isInstanceOf(SocioYaRegistrado.class);

        //verificamos que no se pueda añadir un socio igual al otro usuario ya registrado.
        Socio socio = servicioClub.login("socio_prueba@club.com", "password123");
        assertThrows(SocioYaRegistrado.class, () -> servicioClub.anadirSocio(socio));

        //verificamos que se pueda añadir un socio no registrado.
        Socio socioNoRegistrado = new Socio("Socio", "-", "socio_no_registrado@club.com", "623456789", "password123");
        assertDoesNotThrow(() -> servicioClub.anadirSocio(socioNoRegistrado));
    }

    @Test
    @DirtiesContext
    void testCrearActividad() {
        //actividad bien creada.
        Actividad actividad2 = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        Actividad actividadMalHecha = new Actividad("Actividad de prueba", "Actividad de prueba", 10,
                10, LocalDate.now().plusDays(7), LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10));

        Socio direccion = servicioClub.login("admin@club.es", "ElAdMiN"),
              noDireccion = servicioClub.login("socio_prueba@club.com", "password123");

        //comprobación actividades mal creadas.
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);
        actividadMalHecha.setFechaInicioInscripcion(LocalDate.now().plusDays(1))
                        .setFechaCelebracion(LocalDate.now()).setFechaFinInscripcion(LocalDate.now().plusDays(10));
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);
        actividadMalHecha.setFechaCelebracion(LocalDate.now().plusDays(7));
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividadMalHecha)).isInstanceOf(FechaNoValida.class);

        /*Comprobaciones de que el usuario que quiera añadir una actividad tenga permisos de administrador para una
         actividad a añadir válida.*/
        assertThatThrownBy(() -> servicioClub.crearActividad(noDireccion, actividad2)).isInstanceOf(OperacionDeDireccion.class);

        //Se añade correctamente la actividad.
        assertDoesNotThrow(() -> servicioClub.crearActividad(direccion, actividad2));

        /*Comprobaciones de que el usuario que quiera añadir una actividad tenga permisos de administrador para una
         actividad a añadir no válida.*/
        assertThatThrownBy(() -> servicioClub.crearActividad(noDireccion, actividad2)).isInstanceOf(OperacionDeDireccion.class);

        //No acepta actividades repetidas.
        assertThatThrownBy(() -> servicioClub.crearActividad(direccion, actividad2)).isInstanceOf(ActividadYaExistente.class);
    }

    //@todo completar el test.
    @Test
    @DirtiesContext
    void testMarcarCuotaPagada() {
        Socio direccion = servicioClub.login("admin@club.es", "ElAdMiN");
        Socio socioTest = servicioClub.login("socio_prueba@club.com", "password123");

        //Compruebo que se hace como administrador.
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(socioTest, direccion)).isInstanceOf(OperacionDeDireccion.class);

        //compruebo que funcione con los datos correctos.
        assertDoesNotThrow(() -> servicioClub.marcarCuotaPagada(direccion, socioTest));

        //Compruebo que el socio no tuviera ya pagada la cuota.
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(direccion, socioTest)).isInstanceOf(PagoYaRealizado.class);

        //Compruebo que el socio exista en el sistema (en nuestro caso el administrador no está en memoria con el resto de socios).
        assertThatThrownBy(() -> servicioClub.marcarCuotaPagada(direccion, direccion)).isInstanceOf(SocioNoRegistrado.class);

    }

    @Test
    @DirtiesContext
    void testCrearNuevaTemporada() {

        //compruebo que no hay temporada creada ya existente
        try{
            servicioClub.crearNuevaTemporada();

        }catch (TemporadaYaExistente e){
            fail("Se esperaba que no se lanzara TemporadaYaExistente, pero se lanzo");
        }
        //assertThrows(TemporadaYaExistente.class,() -> {servicioClub.crearNuevaTemporada();});
    }

    @Test
    @DirtiesContext
    void testRealizarSolicitud() {

        //compruebo que no hay actividad creada ya existente
        Actividad act=new Actividad();
        Socio soc=new Socio();
        assertThrows(NoHayActividades.class,() -> {servicioClub.realizarSolicitud(0,act,soc);});

    }




}
