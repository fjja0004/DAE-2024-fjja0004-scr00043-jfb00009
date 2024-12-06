package es.ujaen.dae.clubsocios.rest.dto;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Solicitud;
import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.repositorios.RepositorioSocios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Mapeador {
    @Autowired
    RepositorioSocios repositorioSocios;

    public DTOSocio dtoSocio(Socio socio) {
        return new DTOSocio(
                socio.getNombre(),
                socio.getApellidos(),
                socio.getEmail(), socio.getTelefono(), "");
    }

    public Socio entidadSocio(DTOSocio dtoSocio) {
        return new Socio(dtoSocio.nombre(),
                dtoSocio.apellidos(),
                dtoSocio.email(),
                dtoSocio.tlf(),
                dtoSocio.clave());
    }

    public DTOActividad dtoActividad(Actividad actividad) {
        return new DTOActividad(actividad.getId(),
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getPrecio(),
                actividad.getPlazas(),
                actividad.getPlazasOcupadas(),
                actividad.getFechaInicioInscripcion(),
                actividad.getFechaFinInscripcion(),
                actividad.getFechaCelebracion());
    }

    public Actividad entidadActividad(DTOActividad dtoActividad) {

        return new Actividad(dtoActividad.titulo(),
                dtoActividad.descripcion(),
                dtoActividad.precio(),
                dtoActividad.plazas(),
                dtoActividad.fechaInicioInscripcion(),
                dtoActividad.fechaFinInscripcion(),
                dtoActividad.fechaCelebracion());
    }

    public Temporada entidadTemporada(DTOTemporada dtoTemporada) {
        return new Temporada();
    }

    public DTOTemporada dtoTemporada(Temporada temporada) {
        return new DTOTemporada(temporada.getAnio());
    }

    public Solicitud entidadSolicitud(DTOSolicitud dtosolicitud) {
        return new Solicitud();
        //creo que aqui deberiamos de pasarle algo
        //pero no tenemos  constructores en solictud
        //con los valores que tiene un DTO
    }

    public DTOSolicitud dtoSolicitud(Solicitud solicitud) {
        return new DTOSolicitud(solicitud.getId(),
                solicitud.getnAcompanantes(),
                LocalDate.now(),
                solicitud.getPlazasAceptadas());
    }

}
