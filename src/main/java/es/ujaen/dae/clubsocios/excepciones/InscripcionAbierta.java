package es.ujaen.dae.clubsocios.excepciones;

public class InscripcionAbierta extends RuntimeException {
    public InscripcionAbierta() {
        super("Todavía es posible inscribirse");
    }
}
