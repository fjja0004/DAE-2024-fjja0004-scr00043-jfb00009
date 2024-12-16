package es.ujaen.dae.clubsocios.rest;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubsocios.excepciones.SolicitudYaRealizada;
import es.ujaen.dae.clubsocios.rest.dto.DTOActividad;
import es.ujaen.dae.clubsocios.rest.dto.DTOSocio;
import es.ujaen.dae.clubsocios.rest.dto.DTOTemporada;
import es.ujaen.dae.clubsocios.rest.dto.Mapeador;
import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/club")
public class ControladorClub {
    @Autowired
    Mapeador mapeador;
    @Autowired
    ServicioClub servicioClub;

    Socio admin;

    @PostConstruct
    void loginDireccion() {
        admin = servicioClub.login("admin@club.com", "admin");
    }

    //Mapeado global de excepciones de validación de beans
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionContraintViolationException() {
    }

    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DTOSocio socio) {
        try {
            servicioClub.crearSocio(mapeador.entidadNueva(socio));
        } catch (SocioYaRegistrado e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/socios/{email}")
    public ResponseEntity<DTOSocio> loginSocio(@PathVariable String email, @RequestParam String clave) {
        try {
            Socio usuario = servicioClub.login(email, clave);
            return ResponseEntity.ok(mapeador.dto(usuario));
        } catch (SocioNoValido e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

//    @GetMapping("/socios/{email}")
//    public ResponseEntity<DTOSocio> obtenerSocio(@PathVariable String email) {
//        try {
//            Socio socio = servicioClub.buscarSocio(email).orElseThrow(SocioNoValido::new);
//            return ResponseEntity.ok(mapeador.dto(socio));
//        } catch (SocioNoValido e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @PostMapping("/temporadas")
    public ResponseEntity<Void> nuevaTemporada(@RequestBody DTOTemporada temporada) {
        servicioClub.crearTemporada(temporada.anio());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/temporadas")
    public ResponseEntity<List<DTOTemporada>> obtenerTemporadas() {
        return ResponseEntity.ok(servicioClub.buscarTodasTemporadas().stream().map(t -> new DTOTemporada(t.getAnio())).toList());
    }

    @PostMapping("/actividades")
    public ResponseEntity<Void> nuevaActividad(@RequestBody DTOActividad actividad) {
        servicioClub.crearActividad(admin, mapeador.entidadNueva(actividad));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/actividades")
    public ResponseEntity<List<DTOActividad>> obtenerActividadesPorTemporada(@RequestParam int anio) {
        List<Actividad> actividades;
        actividades = servicioClub.buscarActividadesTemporada(anio);
        return ResponseEntity.ok(actividades.stream().map(a -> mapeador.dto(a)).toList());
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<Void> nuevaSolicitud(@RequestBody DTOSocio socio, @RequestBody DTOActividad actividad, @RequestParam int nAcompanantes) {
        try {
            servicioClub.crearSolicitud(mapeador.entidad(socio), mapeador.entidad(actividad), nAcompanantes);
        } catch (SolicitudYaRealizada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
