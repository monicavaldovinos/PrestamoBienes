package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Objeto;
import java.util.List;

public interface IObjetoDao {
    List<Objeto> obtenerTodos();
    boolean create(Objeto objeto);
    boolean update(Objeto objeto);
    List<Objeto> obtenerPorDivision(int idDivision);
}
