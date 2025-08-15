package utez.edu.mx.prestamos_utez.model;

public class Area {
    private int id;
    private String nombre;
    private int idDivision;
    private String division; // nombre de la división (para mostrar en tabla)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdDivision() { return idDivision; }
    public void setIdDivision(int idDivision) { this.idDivision = idDivision; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    @Override public String toString() { return nombre; }
}
