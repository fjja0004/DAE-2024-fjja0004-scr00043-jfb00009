package es.ujaen.dae.clubsocios.excepciones;

public class ContrasenaNoValida extends RuntimeException {
    public ContrasenaNoValida() {
        super("La contrasena no coincide con la del usuario");
    }
}