package es.ujaen.dae.clubsocios.excepciones;

public class PagoYaRealizado extends RuntimeException {
    public PagoYaRealizado() {
        super("El pago ya ha sido realizado");
    }
}