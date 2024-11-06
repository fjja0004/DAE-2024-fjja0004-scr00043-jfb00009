package es.ujaen.dae.clubsocios.excepciones;

public class SocioNoValido extends RuntimeException {
    public SocioNoValido() {
        super("El socio no es válido");
    }
}
