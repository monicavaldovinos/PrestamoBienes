package com.rebup.dao;

import java.util.List;

import com.rebup.model.Objeto;

public interface ObjetoDao {
    List<Objeto> listarTodos();
    List<Objeto> listarDisponibles();
    List<Objeto> listarNoDisponibles();
}
