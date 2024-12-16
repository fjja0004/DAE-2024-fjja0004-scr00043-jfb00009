package es.ujaen.dae.clubsocios.excepciones;

public class ActividadNoRegistrada extends RuntimeException {
    public ActividadNoRegistrada() {
        super("La actividad no está registrada");
    }
}
