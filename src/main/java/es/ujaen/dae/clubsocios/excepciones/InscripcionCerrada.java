package es.ujaen.dae.clubsocios.excepciones;

public class InscripcionCerrada extends RuntimeException {
    public InscripcionCerrada() {
        super("El plazo de inscripción ha finalizado");
    }
}
