package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Area;
import java.util.List;

public interface IAreaDao {
    List<Area> obtenerTodos();
    List<Area> obtenerPorDivision(int idDivision);
    boolean create(Area area);
    boolean update(Area area);
    boolean deleteById(long idArea);
}
