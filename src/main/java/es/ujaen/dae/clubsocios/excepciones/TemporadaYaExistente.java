package es.ujaen.dae.clubsocios.excepciones;

public class TemporadaYaExistente extends RuntimeException {
    public TemporadaYaExistente() {
        super("La temporada ya se encuentra creada");
    }
}
