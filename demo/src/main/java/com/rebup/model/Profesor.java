package com.rebup.model;

public class Profesor{
    private final int id;
    private final String nombre;

    public Profesor(int id, String nombre) { 
        this.id = id; this.nombre = nombre; }
        
    public int getId() { 
        return id; }
    public String getNombre() {
         return nombre; }

    @Override 
    public String toString() { 
        return nombre; } 
}