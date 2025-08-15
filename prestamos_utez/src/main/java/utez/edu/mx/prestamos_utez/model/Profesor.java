package utez.edu.mx.prestamos_utez.model;

public class Profesor {
    private int id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;

    private int idDivision;
    private String division;

    // NUEVO:
    private int idArea;
    private String area;

    public Profesor() {}

    public Profesor(int id, String nombre, String apellidos, String correo,
                    int idDivision, String division, String telefono,
                    int idArea, String area) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.idDivision = idDivision;
        this.division = division;
        this.telefono = telefono;
        this.idArea = idArea;
        this.area = area;
    }

    // getters/setters existentes...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getIdDivision() { return idDivision; }
    public void setIdDivision(int idDivision) { this.idDivision = idDivision; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    // NUEVOS getters/setters
    public int getIdArea() { return idArea; }
    public void setIdArea(int idArea) { this.idArea = idArea; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
}
