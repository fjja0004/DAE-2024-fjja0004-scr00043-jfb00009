package es.ujaen.dae.clubsocios.entidades;

import es.ujaen.dae.clubsocios.excepciones.SocioNoValido;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Socio {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellidos;
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "El email no es válido")
    @Id
    @Email
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Socio socio = (Socio) o;
        return getEmail().equals(socio.getEmail());
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }

    public String getEmail() {return email;}

    public String getClave() {return clave;}

    public boolean isCuotaPagada() {return cuotaPagada;}

    public void setCuotaPagada(boolean cuotaPagada) {this.cuotaPagada = cuotaPagada;}

    /**
     * @brief Comprueba si la clave introducida es correcta
     * @param clave clave del socio
     * @return true si la clave es correcta
     * @throws SocioNoValido si la clave no es correcta
     */
    public boolean comprobarCredenciales(String clave) {
        if (this.clave.equals(clave)) {
            return true;
        }
        throw new SocioNoValido();
    }

}
