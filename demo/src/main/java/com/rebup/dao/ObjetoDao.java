package com.rebup.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.rebup.model.Objeto;

public class ObjetoDao {
    private Connection conn;

    public ObjetoDao(Connection conn) {
        this.conn = conn;
    }

    public List<Objeto> listarTodos() {
        List<Objeto> lista = new ArrayList<>();
        String sql = "SELECT * FROM OBJETO";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapObjeto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public List<Objeto> listarDisponibles() {
    List<Objeto> lista = new ArrayList<>();
    String sql = "SELECT * FROM OBJETO WHERE ESTADO = 'DISPONIBLE'";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            lista.add(mapObjeto(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

public List<Objeto> listarNoDisponibles() {
    List<Objeto> lista = new ArrayList<>();
    String sql = "SELECT * FROM OBJETO WHERE ESTADO != 'DISPONIBLE'";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            lista.add(mapObjeto(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}



    private Objeto mapObjeto(ResultSet rs) throws SQLException {
        Objeto obj = new Objeto();
        obj.setIdObjeto(rs.getInt("ID_OBJETO"));
        obj.setNombre(rs.getString("NOMBRE"));
        obj.setTipo(rs.getString("TIPO"));
        obj.setNumeroSerie(rs.getString("NUMERO_SERIE"));
        obj.setEstado(rs.getString("ESTADO"));
        obj.setCantidad(rs.getInt("CANTIDAD"));
        obj.setImagenUrl(rs.getString("IMAGEN_URL"));
        obj.setDescripcion(rs.getString("DESCRIPCION"));
        obj.setNumeroInventario(rs.getString("NUMERO_INVENTARIO"));
        return obj;
    }
}

