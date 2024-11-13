package es.ujaen.dae.clubsocios.app;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages="es.ujaen.dae.clubsocios.*")
@EntityScan(basePackages = "src.main.java.entidades")
@EnableScheduling
public class ClubSocios {
    public static void main(String[] args) {
        SpringApplication.run(ClubSocios.class);
    }
}
