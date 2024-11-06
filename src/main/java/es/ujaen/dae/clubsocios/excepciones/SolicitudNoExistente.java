package es.ujaen.dae.clubsocios.excepciones;

public class SolicitudNoExistente extends RuntimeException {
    public SolicitudNoExistente() {
        super("La solicitud no existe");
    }
}
