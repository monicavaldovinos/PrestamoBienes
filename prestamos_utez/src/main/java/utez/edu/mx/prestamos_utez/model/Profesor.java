package utez.edu.mx.prestamos_utez.model;

public class Profesor {
    private int id;
    private String nombre;
    private String division;

    public Profesor() {}

    public Profesor(int id, String nombre, String division) {
        this.id = id;
        this.nombre = nombre;
        this.division = division;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
