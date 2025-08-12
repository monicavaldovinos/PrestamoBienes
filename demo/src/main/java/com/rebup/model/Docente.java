package com.rebup.model;

public class Docente {
    private int idDocente;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;

    public Docente(int idDocente, String nombre, String apellidos, String correo, String telefono) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
    }

    public int getIdDocente() { return idDocente; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
}
