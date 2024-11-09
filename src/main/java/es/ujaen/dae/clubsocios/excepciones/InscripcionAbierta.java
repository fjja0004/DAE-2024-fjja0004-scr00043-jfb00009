package es.ujaen.dae.clubsocios.excepciones;

public class InscripcionAbierta extends RuntimeException {
    public InscripcionAbierta() {
        super("El plazo de inscripción todavía no ha finalizado");
    }
}
