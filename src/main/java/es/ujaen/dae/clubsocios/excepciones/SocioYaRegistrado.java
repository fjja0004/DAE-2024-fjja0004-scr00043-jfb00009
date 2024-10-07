package es.ujaen.dae.clubsocios.excepciones;

public class SocioYaRegistrado extends RuntimeException {
    public SocioYaRegistrado() {
        super("El socio ya está registrado");
    }
}
