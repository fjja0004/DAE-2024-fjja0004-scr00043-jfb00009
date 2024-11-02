package es.ujaen.dae.clubsocios.excepciones;

public class TemporadaNoExistente extends RuntimeException {
    public TemporadaNoExistente() {
        super("La temporada no se encuentra creada");
    }
}
