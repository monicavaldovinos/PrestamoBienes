package com.rebup.dao;

import com.rebup.model.Docente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDaoImpl implements DocenteDao {

    private Connection conn;

    public DocenteDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Docente> listarTodos() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT * FROM DOCENTES";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Docente(
                    rs.getInt("ID_DOCENTE"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDOS"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
