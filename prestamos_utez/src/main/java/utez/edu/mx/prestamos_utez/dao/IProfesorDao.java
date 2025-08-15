package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Profesor;
import java.util.List;

public interface IProfesorDao {
    List<Profesor> obtenerTodos();
    boolean create(Profesor profesor);
    boolean update(Profesor profesor);
    boolean deleteById(int id);

    // <- para que el @Override sea válido:
    List<Profesor> obtenerPorDivision(int idDivision);
}
