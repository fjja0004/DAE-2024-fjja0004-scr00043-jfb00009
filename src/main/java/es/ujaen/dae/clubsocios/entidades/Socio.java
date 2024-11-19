package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
public class Socio {
    @Id
    @PositiveOrZero
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellidos;
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "El email no es válido")
    private String email;
    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$", message = "El teléfono no es válido")
    private String telefono;
    @NotBlank
    private String clave;
    private boolean cuotaPagada;


    /**
     * @brief Constructor por defecto de la clase Socio
     */
    public Socio() {
        nombre = "nombre";
        apellidos = "apellidos";
        email = "email@email.com";
        telefono = "123456789";
        clave = "clave";
        cuotaPagada = false;
    }

    /**
     * @param nombre
     * @param apellidos
     * @param email
     * @param telefono
     * @param clave
     * @brief Contructor parametrizado de la clase Socio
     */
    public Socio(String nombre, String apellidos, String email, String telefono, String clave) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.clave = clave;
        this.cuotaPagada = false;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getClave() {
        return clave;
    }

    public boolean isCuotaPagada() {
        return cuotaPagada;
    }

    public void setCuotaPagada(boolean cuotaPagada) {
        this.cuotaPagada = cuotaPagada;
    }

    public boolean comprobarCredenciales(String clave) {
        if (this.clave.equals(clave)) {
            return true;
        }
        throw new SocioNoValido();
    }


}
