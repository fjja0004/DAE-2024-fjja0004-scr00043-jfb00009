package es.ujaen.dae.clubsocios.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages="es.ujaen.dae.clubsocios.*")
@EntityScan(basePackages = "es.ujaen.dae.clubsocios.entidades")
//@EnableScheduling
public class ClubSocios {
    public static void main(String[] args) {
        SpringApplication.run(ClubSocios.class);
    }
}
