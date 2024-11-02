package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.NoHayActividades;
import es.ujaen.dae.clubsocios.objetosValor.Actividad;
import jakarta.validation.constraints.Positive;

import java.util.LinkedList;
import java.util.List;

public class Temporada {
    @Positive
    private int anio;
    private List<Actividad> actividades;

    /**
     * @brief Constructor por defecto de la clase Temporada
     */
    public Temporada() {
        actividades = new LinkedList<>();
    }

    /**
     * @param anio año de la temporada
     * @brief Constructor parametrizado de la clase Temporada
     * @param anio año de la temporada
     */
    public Temporada(int anio) {
        this.anio = anio;
        actividades = new LinkedList<>();
    }

//    /**
//     * @brief Crear una actividad, si es válida.
//     * @param actividad actividad a crear.
//     * @exception ActividadYaExistente Lanza una excepción si ya existe la actividad.
//     */
//    public void crearActividad(@Valid Actividad actividad) {
//
//        try {
//            if (buscarActividadPorTitulo(actividad.getTitulo()) != null){
//                throw new ActividadYaExistente();
//            }
//        } catch (NoHayActividades ignored) {}
//
//        actividades.add(actividad);
//    }

    /**
     * @brief Buscar una actividad por su título
     * @param titulo título de la actividad
     * @return la actividad con el título dado
     * @exception NoHayActividades lanza una excepción si no existe ninguna actividad.
     */
    public Actividad buscarActividadPorTitulo(String titulo) {
        if (actividades.isEmpty()){
            throw new NoHayActividades();
        }
        for (Actividad actividad : actividades) {
            if (actividad.getTitulo().equals(titulo)) {
                return actividad;
            }
        }
        return null;
    }

}
