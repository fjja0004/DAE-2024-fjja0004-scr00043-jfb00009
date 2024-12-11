package es.ujaen.dae.clubsocios.seguridad;

import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicioCredencialesSocio {

    @Autowired
    ServicioClub servicioClub;

}
