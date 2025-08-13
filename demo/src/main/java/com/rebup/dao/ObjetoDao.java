package com.rebup.dao;

import java.sql.SQLException;
import java.util.List;

import com.rebup.model.Objeto;

public interface ObjetoDao {
    List<Objeto> listarTodos();
    List<Objeto> listarDisponibles();
    List<Objeto> listarNoDisponibles();
    void actualizarCantidadObjeto(int idObjeto, int cantidad) throws SQLException;
    void insertarObjeto(Objeto obj) throws SQLException;
    void actualizarCantidadYEstado(int idObjeto, int cantidad) throws SQLException;
    Objeto obtenerPorId(int idObjeto);
}
