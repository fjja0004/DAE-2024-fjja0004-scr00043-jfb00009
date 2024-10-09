package es.ujaen.dae.clubsocios.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.HashMap;

public class Actividad {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @Positive
    private int precio;
    @Positive
    private int plazas;

    private LocalDate fechaCelebracion;

    private LocalDate fechaInicioInscripcion;

    private LocalDate fechaFinInscripcion;

    private HashMap<String, Solicitud> solicitudes;

    /**
     * @brief Constructor por defecto de la clase Actividad
     */
    public Actividad() {
        this.titulo = "";
        this.descripcion = "";
        this.precio = 0;
        this.plazas = 0;
        this.fechaCelebracion = LocalDate.now();
        this.fechaInicioInscripcion = LocalDate.now();
        this.fechaFinInscripcion = LocalDate.now();
        this.solicitudes = new HashMap<>();
    }

    /**
     * @param titulo
     * @param descripcion
     * @param precio
     * @param plazas
     * @param fechaCelebracion
     * @param fechaInicioInscripcion
     * @param fechaFinInscripcion
     * @brief Constructor parametrizado de la clase Actividad
     */
    public Actividad(String titulo, String descripcion, int precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public LocalDate getFechaCelebracion() {
        return fechaCelebracion;
    }

    public void setFechaCelebracion(LocalDate fechaCelebracion) {
        this.fechaCelebracion = fechaCelebracion;
    }

    public LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public void setFechaInicioInscripcion(LocalDate fechaInicioInscripcion) {
        this.fechaInicioInscripcion = fechaInicioInscripcion;
    }

    public LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    public void setFechaFinInscripcion(LocalDate fechaFinInscripcion) {
        this.fechaFinInscripcion = fechaFinInscripcion;
    }
}
