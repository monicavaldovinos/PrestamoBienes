package com.rebup.model;

public class Objeto {
    
    private int idObjeto;
    private String nombre;
    private String tipo;
    private String numeroSerie;
    private String estado;
    private int cantidad;
    private String imagenUrl;
    private String descripcion;
    private String numeroInventario;

    public Objeto(int idObjeto, String nombre, String tipo, String numeroSerie, String estado,
                  int cantidad, String imagenUrl, String descripcion, String numeroInventario) {
        this.idObjeto = idObjeto;
        this.nombre = nombre;
        this.tipo = tipo;
        this.numeroSerie = numeroSerie;
        this.estado = estado;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
        this.numeroInventario = numeroInventario;
    }

    // Getters
    public int getIdObjeto() { return idObjeto; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getNumeroSerie() { return numeroSerie; }
    public String getEstado() { return estado; }
    public int getCantidad() { return cantidad; }
    public String getImagenUrl() { return imagenUrl; }
    public String getDescripcion() { return descripcion; }
    public String getNumeroInventario() { return numeroInventario; }
}
