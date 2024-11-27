package es.ujaen.dae.clubsocios.rest.dto;

import java.time.LocalDate;

public record DTOSolicitud (
    int id,
    int nAcompanantes,
    LocalDate fecha,
    int plazasAceptadas)
{ }
