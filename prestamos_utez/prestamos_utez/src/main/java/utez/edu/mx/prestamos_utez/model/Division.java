package utez.edu.mx.prestamos_utez.model;

public class Division {
    private int id;
    private String nombre;
    private String asignatura; // opcional

    public Division() {}

    public Division(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    @Override
    public String toString() {
        return nombre; // Esto es lo que se muestra en el ComboBox
    }
}

