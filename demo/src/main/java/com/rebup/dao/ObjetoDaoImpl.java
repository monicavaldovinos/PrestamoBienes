package com.rebup.dao;

import com.rebup.model.Objeto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoDaoImpl implements ObjetoDao {

    private Connection conn;

    public ObjetoDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Objeto> listarTodos() {
        return obtenerObjetos("SELECT * FROM OBJETO");
    }

    @Override
    public List<Objeto> listarDisponibles() {
        return obtenerObjetos("SELECT * FROM OBJETO WHERE ESTADO = 'Disponible'");
    }

    @Override
    public List<Objeto> listarNoDisponibles() {
        return obtenerObjetos("SELECT * FROM OBJETO WHERE ESTADO != 'Disponible'");
    }

    private List<Objeto> obtenerObjetos(String sql) {
        List<Objeto> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Objeto(
                    rs.getInt("ID_OBJETO"),
                    rs.getString("NOMBRE"),
                    rs.getString("TIPO"),
                    rs.getString("NUMERO_SERIE"),
                    rs.getString("ESTADO"),
                    rs.getInt("CANTIDAD"),
                    rs.getString("IMAGEN_URL"),
                    rs.getString("DESCRIPCION"),
                    rs.getString("NUMERO_INVENTARIO")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
