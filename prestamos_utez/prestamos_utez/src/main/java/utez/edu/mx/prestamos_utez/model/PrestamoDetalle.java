package utez.edu.mx.prestamos_utez.model;

import java.sql.Date;

public class PrestamoDetalle {
    private int idDetalle;
    private String articulo;
    private String numSerie;
    private String numInventario;
    private Date fechaEntrega;   // null si pendiente
    private String estado;       // PENDIENTE | ENTREGADO


    public PrestamoDetalle(int idDetalle, String articulo, String numSerie, String numInventario, Date fechaEntrega, String estado) {
        this.idDetalle = idDetalle;
        this.articulo = articulo;
        this.numSerie = numSerie;
        this.numInventario = numInventario;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getNumInventario() {
        return numInventario;
    }

    public void setNumInventario(String numInventario) {
        this.numInventario = numInventario;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
