package com.rebup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rebup.config.DBConnection;
import com.rebup.model.Profesor;

public class ProfesorDao {
    public List<Profesor> listar() {
        String sql = "SELECT ID_DOCENTE, NOMBRE || ' ' || APELLIDOS AS NOMBRE "
                   + "FROM DOCENTE d ORDER BY 2";
        List<Profesor> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Profesor(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
}
