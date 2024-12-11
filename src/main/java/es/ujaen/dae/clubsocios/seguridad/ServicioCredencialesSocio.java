package es.ujaen.dae.clubsocios.seguridad;

import es.ujaen.dae.clubsocios.entidades.Socio;
import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import es.ujaen.dae.clubsocios.servicios.ServicioClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class ServicioCredencialesSocio {

    @Autowired
    ServicioClub servicioClub;

    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Socio socio=servicioClub.buscarSocio(userName).orElseThrow(() -> new UsernameNotFoundException(""));
        return User.withUsername(socio.getEmail())
                .password(socio.getClave())
                .roles(socio.getNombre().equals("direccion") ? "DIRECCION": "CLIENTE")
                .build();

    }

}
