package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Division;
import java.util.List;

public interface IDivisionDao {
    List<Division> obtenerDivisiones();   // NO throws aquí
    boolean create(Division d);
    boolean update(Division d);
    boolean deleteById(int idDivision);
}
