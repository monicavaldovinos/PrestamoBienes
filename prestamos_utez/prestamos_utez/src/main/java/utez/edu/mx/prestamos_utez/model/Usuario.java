package utez.edu.mx.prestamos_utez.model;

public class Usuario {
    private int idUsuario;      // ID_USUARIO
    private String nombre;      // NOMBRE
    private String apellidos;   // APELLIDOS
    private String correo;      // CORREO (lo usarás como username)
    private String contrasena;  // CONTRASENA (hash)
    private int idRol;          // ID_ROL (FK)
    private String nombreRol;   // (via JOIN para mostrar "ADMIN"/"ENCARGADO")
    private String nombreDivision; // Nombre de la división (puede ser null)
    private int idDivision; // ID_DIVISION (FK)

    public Usuario() {

    }

    public Usuario(int idUsuario, String nombre, String apellidos, String correo, String contrasena, int idRol, String nombreRol, String nombreDivision, int idDivision) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.nombreDivision = nombreDivision;
        this.idDivision = idDivision;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getNombreDivision() {
        return nombreDivision;
    }

    public void setNombreDivision(String nombreDivision) {
        this.nombreDivision = nombreDivision;
    }

    public int getIdDivision() {
        return idDivision;
    }

    public void setIdDivision(int idDivision) {
        this.idDivision = idDivision;
    }
}
