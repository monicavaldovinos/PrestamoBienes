package utez.edu.mx.prestamos_utez.model;

import java.sql.Date;
import java.util.logging.Logger;

public class HistorialPrestamo {
    private int idPrestamo;
    private String profesor;
    private int cantidad;
    private Date fechaPrestamo;
    private String estado;
    private static final Logger logger = Logger.getLogger(HistorialPrestamo.class.getName());


    public HistorialPrestamo(int idPrestamo, String profesor, int cantidad, Date fechaPrestamo, String estado) {
        this.idPrestamo = idPrestamo;
        this.profesor = profesor;
        this.cantidad = cantidad;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = estado;
    }

    public HistorialPrestamo() {
        // Constructor vacío para uso en DAO
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
