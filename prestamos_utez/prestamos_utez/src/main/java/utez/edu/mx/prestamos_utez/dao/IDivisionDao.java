package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Division;
import java.sql.SQLException;
import java.util.List;

public interface IDivisionDao {
    List<Division> listar() throws SQLException;
}
