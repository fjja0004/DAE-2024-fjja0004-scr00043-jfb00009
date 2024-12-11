package es.ujaen.dae.clubsocios.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class ServicioSeguridad {

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.disable())
                .httpBasic(httpBasic -> httpBasic.realmName("club"))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/socios/{email}")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('SOCIO') and #email == principal.username)"))
                        .requestMatchers(HttpMethod.POST, "/actividades").hasRole("DIRECCION")
                        .requestMatchers("/club/**").permitAll()
                )
                .build();


    }
}
