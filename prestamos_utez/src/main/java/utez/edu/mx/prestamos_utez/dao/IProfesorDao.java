package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Profesor;
import java.util.List;

public interface IProfesorDao {
    List<Profesor> obtenerTodos();
    boolean create(Profesor profesor);
}