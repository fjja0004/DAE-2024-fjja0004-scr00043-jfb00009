package es.ujaen.dae.clubsocios.excepciones;

public class DemasiadosAcompanantes extends RuntimeException {
    public DemasiadosAcompanantes() {
        super("Se han intentado asignar más acompañantes de los solicitados");
    }
}
