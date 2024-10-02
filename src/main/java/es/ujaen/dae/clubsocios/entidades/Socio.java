package src.main.java.es.ujaen.dae.clubsocios.entidades;
public enum Pagos {
    noPagado, pendiente, pagado;
}

public class Socio {

    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String clave;
    private Boolean admin;
    private Pagos cuotaPagada;

    /**
     * @brief Constructor para la creación del Administrador.
     */
    public Socio() {
        this.nombre = "admin";
        this.apellidos = "admin";
        this.email = "admin_club@gmail.com";
        this.telefono = "111111111";
        this.clave = "admin";
        this.admin = true;
        this.cuotaPagada = Pagos.pagado;
    }

    /**
     * @brief Contructor parametrizado para crear socios.
     * @param nombre
     * @param apellidos
     * @param email
     * @param telefono
     * @param clave
     */
    public Socio(String nombre, String apellidos, String email, String telefono, String clave) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.clave = clave;
        this.admin = false;
        this.cuotaPagada = Pagos.noPagado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public src.main.java.es.ujaen.dae.clubsocios.entidades.Pagos getCuotaPagada() {
        return cuotaPagada;
    }

    public void setCuotaPagada(src.main.java.es.ujaen.dae.clubsocios.entidades.Pagos cuotaPagada) {
        this.cuotaPagada = cuotaPagada;
    }
}
