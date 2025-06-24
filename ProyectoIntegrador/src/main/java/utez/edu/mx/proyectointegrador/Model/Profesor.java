package utez.edu.mx.proyectointegrador.model;

import javafx.beans.property.SimpleStringProperty;

public class Profesor {
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleStringProperty division = new SimpleStringProperty();

    public Profesor(String nombre, String division) {
        this.nombre.set(nombre);
        this.division.set(division);
    }
    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getDivision() {
        return division.get();
    }

    public void setDivision(String division) {
        this.division.set(division);
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public SimpleStringProperty divisionProperty() {
        return division;
    }

}
