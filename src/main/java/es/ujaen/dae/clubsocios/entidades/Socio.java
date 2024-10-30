package es.ujaen.dae.clubsocios.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Socio {
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
        id = -1;
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
        this.id = -1;
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

    public void setId(int i) {
        this.id = i;
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


}
