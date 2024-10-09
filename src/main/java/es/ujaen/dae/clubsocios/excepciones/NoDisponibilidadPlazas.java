package es.ujaen.dae.clubsocios.excepciones;

public class NoDisponibilidadPlazas extends RuntimeException {
    public NoDisponibilidadPlazas() {
        super("No hay plazas disponibles");
    }
}
