package es.ujaen.dae.clubsocios.excepciones;

public class ActividadYaExistente extends RuntimeException {
    public ActividadYaExistente() {
        super("Esta actividad ya existe");
    }
}
