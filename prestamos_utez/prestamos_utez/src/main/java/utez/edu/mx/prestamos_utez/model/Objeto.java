package utez.edu.mx.prestamos_utez.model;

public class Objeto {
    private int id;
    private String nombre;
    private String numeroInventario;
    private String numeroSerie;
    private String descripcion;
    private String imagenUrl;
    private int cantidad;
    private int idDivision; // ID de la división en BD
    private String division; // Nombre de la división

    public Objeto() {}

    public Objeto(int id, String nombre, String numeroInventario, String numeroSerie, String descripcion,
                  String imagenUrl, int cantidad, int idDivision, String division) {
        this.id = id;
        this.nombre = nombre;
        this.numeroInventario = numeroInventario;
        this.numeroSerie = numeroSerie;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.cantidad = cantidad;
        this.idDivision = idDivision;
        this.division = division;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroInventario() { return numeroInventario; }
    public void setNumeroInventario(String numeroInventario) { this.numeroInventario = numeroInventario; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getIdDivision() { return idDivision; }
    public void setIdDivision(int idDivision) { this.idDivision = idDivision; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
}

