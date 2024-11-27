package es.ujaen.dae.clubsocios.rest.dto;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.entidades.Temporada;
import es.ujaen.dae.clubsocios.repositorios.RepositorioSocios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mapeador {
    @Autowired
    RepositorioSocios repositorioSocios;
    public DTOSocio dto(Socio socio){
        //TODO AÑADIR EN EL SOCIO LOS METODOS PARA PODER ACCEDER
        return new DTOSocio();
    }
    public Socio entidad(DTOSocio dtoSocio){
        //TODO AÑADIR EN EL DTOsocio LOS METODOS NECESARIOS
        return new Socio();
    }
    public DTOActividad dtoActividad(Actividad actividad){
    //TODO AÑADIR METODOS A SU clase
        return new DTOActividad(actividad.getId(), actividad.getTitulo(), );
    }
    public Actividad entidad(DTOActividad dtoActividad){

        return new Actividad(dtoActividad.titulo(),dtoActividad.descripcion(),dtoActividad.precio(),dtoActividad.plazas(),dtoActividad.fechaInicioInscripcion(),dtoActividad.fechaFinInscripcion(),dtoActividad.fechaCelebracion());
    }



}
