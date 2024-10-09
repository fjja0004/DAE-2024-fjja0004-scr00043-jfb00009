package es.ujaen.dae.clubsocios.excepciones;

public class SocioNoRegistrado extends RuntimeException {
    public SocioNoRegistrado() {
        super("El socio no está registrado");
    }
}
