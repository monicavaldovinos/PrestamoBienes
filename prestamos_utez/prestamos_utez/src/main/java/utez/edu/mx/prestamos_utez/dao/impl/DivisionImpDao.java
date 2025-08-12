package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.model.Division;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionImpDao {
    private static final String SQL =
            "SELECT ID_DIVISION, NOMBRE FROM DIVISION ORDER BY NOMBRE";

    public List<Division> obtenerDivisiones() throws SQLException {
        List<Division> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Division(
                        rs.getInt("ID_DIVISION"),
                        rs.getString("NOMBRE")
                ));
            }
        }
        return lista;
    }
}
