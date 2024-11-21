package es.ujaen.dae.clubsocios.excepciones;

public class ActividadNoEncontrada extends RuntimeException {
    public ActividadNoEncontrada() {
        super("La actividad no ha sido encontrada o no existe");
    }
}
