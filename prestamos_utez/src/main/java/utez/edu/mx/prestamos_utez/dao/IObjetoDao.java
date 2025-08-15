package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Objeto;
import utez.edu.mx.prestamos_utez.model.ObjetoArea;
import utez.edu.mx.prestamos_utez.model.ObjetoItem;

import java.util.List;

public interface IObjetoDao {
    List<Objeto> obtenerTodos();

    boolean create(Objeto objeto);
    boolean create(Objeto objeto, List<ObjetoItem> items);
    boolean create(Objeto objeto, List<ObjetoItem> items, List<ObjetoArea> distribucionAreas);

    boolean update(Objeto objeto);

    List<Objeto> obtenerPorDivision(int idDivision);

    boolean deleteById(long idObjeto);
}
