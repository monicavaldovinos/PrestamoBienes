package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IDivisionDao;
import utez.edu.mx.prestamos_utez.model.Division;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionImpDao implements IDivisionDao {

    private static final String SEL_ALL = """
        SELECT ID_DIVISION, NOMBRE
          FROM DIVISION
         ORDER BY NOMBRE
    """;

    @Override
    public List<Division> obtenerDivisiones() {
        List<Division> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SEL_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Division d = new Division();
                d.setId(rs.getInt("ID_DIVISION"));
                d.setNombre(rs.getString("NOMBRE"));
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // loguea y devuelve lista vacía
        }
        return lista;
    }

    /** Alias opcional para compatibilidad con código viejo que use 'obtenerTodos()'. */
    public List<Division> obtenerTodos() {
        return obtenerDivisiones();
    }

    public boolean create(Division d) {
        String sql = "INSERT INTO DIVISION (NOMBRE) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    @Override
    public boolean update(Division d) {
        String sql = "UPDATE DIVISION SET NOMBRE=? WHERE ID_DIVISION=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setInt(2, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(int idDivision) {
        String sql = "DELETE FROM DIVISION WHERE ID_DIVISION = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
