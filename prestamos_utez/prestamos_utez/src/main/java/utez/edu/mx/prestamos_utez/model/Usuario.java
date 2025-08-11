package utez.edu.mx.prestamos_utez.model;

public class Usuario {
    private int id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String idRol;

    public Usuario() {}

    public Usuario(int id, String nombre, String apellidos, String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.idRol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getIdRol() { return idRol; }
    public void setIdRol(String idRol) { this.idRol = idRol; }
}
