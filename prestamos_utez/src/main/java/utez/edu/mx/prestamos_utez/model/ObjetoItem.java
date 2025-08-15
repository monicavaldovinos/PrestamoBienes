package utez.edu.mx.prestamos_utez.model;

public class ObjetoItem {
    private Integer idItem;              // PK de OBJETO_ITEM (puede venir de la BD)
    private Integer idObjeto;            // FK al OBJETO (opcional en el modelo)
    private String  numeroSerie;
    private String  numeroInventario;
    private String  estado;              // p. ej. "DISPONIBLE"
    private Integer idArea;              // <-- NUEVO: área donde está la pieza (nullable)

    public Integer getIdItem() { return idItem; }
    public void setIdItem(Integer idItem) { this.idItem = idItem; }

    public Integer getIdObjeto() { return idObjeto; }
    public void setIdObjeto(Integer idObjeto) { this.idObjeto = idObjeto; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getNumeroInventario() { return numeroInventario; }
    public void setNumeroInventario(String numeroInventario) { this.numeroInventario = numeroInventario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getIdArea() { return idArea; }           // <-- getter que pide el DAO
    public void setIdArea(Integer idArea) { this.idArea = idArea; } // <-- setter
}
