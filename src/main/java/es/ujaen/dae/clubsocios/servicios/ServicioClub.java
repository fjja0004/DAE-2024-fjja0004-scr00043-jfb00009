package es.ujaen.dae.clubsocios.servicios;

import es.ujaen.dae.clubsocios.entidades.Actividad;
import es.ujaen.dae.clubsocios.entidades.Socio;

import java.util.Date;

public class ServicioClub {
    Socio login (String email,String clave){


        return null;
    }


    Boolean anadirSocio(String nombre,String apellido,String telefono,String clave){
        return null;
    }

    Boolean borrarSocio( String email){
        return null;
    }
    Boolean anadirActividad(String titulo, String descripcion, double precio, int nPlazas, Date fechaCelebracion,Date fechaInscripcion){
        return null;
    }
    Boolean borrarActividad(String titulo){
        return null;
    }
    void revisarSolicitudes(){

    }
    void marcarCuotaPagada(Socio socio){

    }
    Actividad buscarActividad(String titulo,int anio){
        return null;
    }

    Boolean realizarSolicitud(int nAcompanantes,Actividad actividad){
        return  null;
    }
    Boolean anadirAcompanante(){
        return null;
    }
    Boolean quitarAcompanante(){
        return null;
    }
    void borrarSolicitud(Actividad actividad){

    }
    void pagarCuota(Socio socio){

    }

    void crearNuevaTemporada(Socio socio){

    }
}
