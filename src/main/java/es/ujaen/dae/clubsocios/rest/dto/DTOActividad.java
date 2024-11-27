package es.ujaen.dae.clubsocios.rest.dto;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record DTOActividad(
        int id,
        String titulo,
        String descripcion,
        int precio,
        int plazas,
        int plazasOcupadas,
        LocalDate fechaInicioInscripcion,
        LocalDate fechaFinInscripcion,
        LocalDate fechaCelebracion
){

}
