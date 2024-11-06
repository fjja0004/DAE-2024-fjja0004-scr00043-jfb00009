package es.ujaen.dae.clubsocios.excepciones;

public class SolicitudNoValida extends RuntimeException {
    public SolicitudNoValida() {
        super("La solicitud no es válida");
    }
}
