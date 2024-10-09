package es.ujaen.dae.clubsocios.excepciones;

public class SolicitudYaRealizada extends RuntimeException {
    public SolicitudYaRealizada() {
        super("La solicitud ya ha sido realizada");
    }
}
