package es.ujaen.dae.clubsocios.rest.dto;

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
) {

}
