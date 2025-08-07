package com.rebup;

public class Bien {
    private String nombre;
    private String imagen;
    private boolean disponible;

    public Bien(String nombre, String imagen, boolean disponible) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.disponible = disponible;
    }

    public String getNombre(){
         return nombre; }
         
    public String getImagen(){
         return imagen; }

    public boolean isDisponible(){
         return disponible; }
}
