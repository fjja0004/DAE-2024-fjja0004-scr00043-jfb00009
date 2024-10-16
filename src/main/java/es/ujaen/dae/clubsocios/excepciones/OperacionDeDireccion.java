package es.ujaen.dae.clubsocios.excepciones;

public class OperacionDeDireccion extends RuntimeException {
    public OperacionDeDireccion() {
        super("Operación restringida a la dirección");
    }

}
