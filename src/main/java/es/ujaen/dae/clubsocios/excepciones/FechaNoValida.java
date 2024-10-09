package es.ujaen.dae.clubsocios.excepciones;

public class FechaNoValida extends RuntimeException {
    public FechaNoValida() {
        super("La fecha no es válida");
    }
}
