package es.ujaen.dae.clubsocios.excepciones;

public class ActividadYaExistente extends RuntimeException {
    public ActividadYaExistente() {
        super("La actividad ya existe");
    }
}
