package utez.edu.mx.prestamos_utez.model;

public class ObjetoArea {
    private Integer idArea;     // área destino
    private int     cantidad;   // cuántas piezas van a esa área

    public ObjetoArea() {}
    public ObjetoArea(Integer idArea, int cantidad) {
        this.idArea = idArea;
        this.cantidad = cantidad;
    }

    public Integer getIdArea() { return idArea; }
    public void setIdArea(Integer idArea) { this.idArea = idArea; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
