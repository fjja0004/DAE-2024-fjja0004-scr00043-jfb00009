package es.ujaen.dae.clubsocios.rest;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubsocios.rest.dto.DTOActividad;
import es.ujaen.dae.clubsocios.rest.dto.DTOSocio;
import es.ujaen.dae.clubsocios.rest.dto.Mapeador;
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
@RequestMapping("/solicitudes")
public class ControladorClub {
@Autowired
    Mapeador mapeador;
@Autowired
ServicioClub servicioClub;

    Socio  admin;
@PostConstruct
    void loginDireccion(){
    admin = servicioClub.login("admin@club.com","admin");
}
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionContraintViolationException(){}

    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DTOSocio socio){
    try{
        servicioClub.crearSocio(mapeador.entidadSocio(socio));
    }catch(SocioYaRegistrado e){
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();

    }
    @GetMapping ("/socios/{email}")
    public ResponseEntity<DTOSocio> loginSocio(@PathVariable String email,@RequestParam String clave){
        try{
            Socio socio= servicioClub.login(email,clave);
            return ResponseEntity.ok(mapeador.dtoSocio(socio));
        }catch (SocioNoValido e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping ("/actividades")
    public ResponseEntity<Void> nuevaActivdad(@RequestBody DTOActividad actividad){
    servicioClub.crearActividad(admin,mapeador.entidadActividad(actividad));

    return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/actividades")
    public ResponseEntity<List<DTOActividad>> buscarActividades(
            @RequestParam int id,
            @RequestParam (required = false)String titulo,
            @RequestParam (required = false)String descripcion,
            @RequestParam (required=false)int precio,
            @RequestParam (required=false)int plazas,
            @RequestParam (defaultValue = "0")LocalDate fechaInicioInscripcion,
            @RequestParam (defaultValue = "0")LocalDate fechaFinInscripcion,
            @RequestParam (defaultValue = "0")LocalDate fechaCelebracion) {

        // Definicion de fechas límite en caso de omisión
        LocalDate desde = LocalDate.of(2023, 1, 1);
        LocalDate hasta = LocalDate.of(2023, 12, 31);

        final var desdeFinal = desde != null ? desde : LocalDate.now();
        final var hastaFinal = hasta != null ? hasta : LocalDate.MAX;
        List<Actividad> actividades;
        actividades= servicioClub.buscarActividadPorId(id).stream().toList();

        return ResponseEntity.ok(actividades.stream().map(a->mapeador.dtoActividad(a)).toList());
    }

}
