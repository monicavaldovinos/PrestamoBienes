package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.model.Division;

import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class DivisionImpDao {
    public List<Division> obtenerDivisiones() {
        List<Division> lista = new ArrayList<>();
        String sql = "SELECT ID_DIVISION, NOMBRE FROM DIVISION";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Division d = new Division();
                d.setId(rs.getInt("ID_DIVISION"));
                d.setNombre(rs.getString("NOMBRE"));
                lista.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

