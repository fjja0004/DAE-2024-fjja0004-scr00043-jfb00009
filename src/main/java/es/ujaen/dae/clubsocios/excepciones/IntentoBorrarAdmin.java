package es.ujaen.dae.clubsocios.excepciones;

public class IntentoBorrarAdmin extends RuntimeException {
    public IntentoBorrarAdmin() {
        super("No se puede borrar un administrador");
    }
}
