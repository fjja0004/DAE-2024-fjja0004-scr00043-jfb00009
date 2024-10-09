package es.ujaen.dae.clubsocios.excepciones;

public class NoHayActividades extends RuntimeException {
    public NoHayActividades() {
        super("No hay actividades disponibles");
    }
}
