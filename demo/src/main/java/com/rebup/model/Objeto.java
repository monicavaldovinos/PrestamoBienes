package com.rebup.model;

public class Objeto {
    
    private int IdObjeto;
    private String Nombre;
    private String Tipo;
    private String NumeroSerie;
    private String Estado;
    private int Cantidad;
    private String ImagenUrl;
    private String Descripcion;
    private String NumeroInventario;

    public Objeto(int idObjeto, String nombre, String tipo, String numeroSerie, String estado,
                  int cantidad, String imagenUrl, String descripcion, String numeroInventario) {
        this.IdObjeto = idObjeto;
        this.Nombre = nombre;
        this.Tipo = tipo;
        this.NumeroSerie = numeroSerie;
        this.Estado = estado;
        this.Cantidad = cantidad;
        this.ImagenUrl = imagenUrl;
        this.Descripcion = descripcion;
        this.NumeroInventario = numeroInventario;
    }

    
    public int getIdObjeto() { return IdObjeto; }
    public String getNombre() { return Nombre; }
    public String getTipo() { return Tipo; }
    public String getNumeroSerie() { return NumeroSerie; }
    public String getEstado() { return Estado; }
    public int getCantidad() { return Cantidad; }
    public String getImagenUrl() { return ImagenUrl; }
    public String getDescripcion() { return Descripcion; }
    public String getNumeroInventario() { return NumeroInventario; }
}