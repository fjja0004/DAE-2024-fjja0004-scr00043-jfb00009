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
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.excepciones.*;
import es.ujaen.dae.clubsocios.rest.dto.*;
import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        admin = servicioClub.login("admin@club.com", "$2a$10$JAfCuJzY1t.zIfTfhX7Zb.ep0zj0J/c4i7LXDa6.cAg0b6ikt94LG");
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
    public ResponseEntity<DTOSocio> obtenerSocio(@PathVariable String email) {
        try {
            Socio socio = servicioClub.buscarSocio(email).orElseThrow(SocioNoValido::new);
            return ResponseEntity.ok(mapeador.dto(socio));
        } catch (SocioNoValido e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/temporadas")
    public ResponseEntity<List<DTOTemporada>> obtenerTemporadas() {
        return ResponseEntity.ok(servicioClub.buscarTodasTemporadas().stream().map(t -> new DTOTemporada(t.getAnio())).toList());
    }

    @PostMapping("/actividades")
    public ResponseEntity<Void> nuevaActividad(@RequestBody DTOActividad actividad) {
        servicioClub.crearActividad(mapeador.entidadNueva(actividad));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/actividades")
    public ResponseEntity<List<DTOActividad>> obtenerActividadesPorTemporada(@RequestParam int anio) {
        List<Actividad> actividades;
        actividades = servicioClub.buscarActividadesTemporada(anio);
        return ResponseEntity.ok(actividades.stream().map(a -> mapeador.dtoActividad(a)).toList());
    }

    @PostMapping("/actividades/{id}/solicitudes")
    public ResponseEntity<DTOSolicitud> nuevaSolicitud(@PathVariable int id, @RequestBody DTOSolicitud solicitud) {
        try {
            Actividad actividad = servicioClub.buscarActividadPorId(id).orElseThrow(ActividadNoRegistrada::new);
            Socio socio = servicioClub.buscarSocio(solicitud.emailSocio()).orElseThrow(SocioNoValido::new);

            return ResponseEntity.status(HttpStatus.CREATED).body(mapeador.dto(servicioClub.crearSolicitud(
                    socio,
                    actividad,
                    solicitud.nAcompanantes()
            )));

        } catch (SocioNoValido | ActividadNoRegistrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SolicitudYaRealizada | InscripcionCerrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/actividades/{id}/solicitudes/{idSolicitud}")
    public ResponseEntity<DTOSolicitud> modificarAcompanantes(@PathVariable int id, @RequestBody DTOSolicitud solicitud) {
        try {
            Actividad actividad = servicioClub.buscarActividadPorId(id).orElseThrow(ActividadNoRegistrada::new);
            Solicitud solicitudEnt = mapeador.entidad(solicitud);

            return ResponseEntity.status(HttpStatus.OK).body(mapeador.dto(servicioClub.modificarSolicitud(
                    actividad,
                    solicitudEnt,
                    solicitud.nAcompanantes()
            )));
        } catch (SolicitudNoExistente | ActividadNoRegistrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InscripcionCerrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @PostMapping ("/temporadas")
    public ResponseEntity<DTOTemporada> nuevaTemporada() {
        servicioClub.crearTemporada(LocalDate.now().getYear());

        return ResponseEntity.status(HttpStatus.CREATED).build();


    }
    @DeleteMapping("/actividades/{id}/solicitudes/{idSolicitud}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable int id, @PathVariable int idSolicitud) {
        try {
            Actividad actividad = servicioClub.buscarActividadPorId(id).orElseThrow(ActividadNoRegistrada::new);
            Solicitud solicitudEnt = servicioClub.buscarSolicitudPorId(id, idSolicitud).orElseThrow(SolicitudNoExistente::new);
            servicioClub.cancelarSolicitud(actividad, solicitudEnt);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ActividadNoRegistrada | SolicitudNoExistente e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InscripcionCerrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/actividades/{id}/solicitudes")
    public ResponseEntity<List<DTOSolicitud>> obtenerSolicitudesActividad(@PathVariable int id) {
        try {
            Actividad actividad = servicioClub.buscarActividadPorId(id).orElseThrow(ActividadNoRegistrada::new);
            return ResponseEntity.ok(servicioClub.buscarSolicitudesDeActividad(admin, actividad).stream().map(s -> mapeador.dto(s)).toList());
        } catch (ActividadNoRegistrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
