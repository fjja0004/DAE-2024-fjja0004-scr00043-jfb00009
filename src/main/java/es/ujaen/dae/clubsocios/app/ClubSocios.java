package es.ujaen.dae.clubsocios.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages="es.ujaen.dae.clubsocios.servicios")
@EnableScheduling
public class ClubSocios {
    public static void main(String[] args) {
        SpringApplication.run(ClubSocios.class);
    }
}
