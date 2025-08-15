package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IAreaDao;
import utez.edu.mx.prestamos_utez.model.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaImplDao implements IAreaDao {

    private static final String SEL_BASE = """
        SELECT a.ID_AREA,
               a.NOMBRE,
               a.ID_DIVISION,
               d.NOMBRE AS DIVISION
          FROM AREA a
          JOIN DIVISION d ON d.ID_DIVISION = a.ID_DIVISION
    """;

    @Override
    public List<Area> obtenerTodos() {
        List<Area> lista = new ArrayList<>();
        String sql = SEL_BASE + " ORDER BY d.NOMBRE, a.NOMBRE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Area> obtenerPorDivision(int idDivision) {
        List<Area> lista = new ArrayList<>();
        String sql = """
        SELECT a.ID_AREA, a.NOMBRE, a.ID_DIVISION
          FROM AREA a
         WHERE a.ID_DIVISION = ?
         ORDER BY a.NOMBRE
    """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Area a = new Area();
                    a.setId(rs.getInt("ID_AREA"));
                    a.setNombre(rs.getString("NOMBRE"));
                    a.setIdDivision(rs.getInt("ID_DIVISION"));
                    lista.add(a);
                }
            }
            System.out.println("[DAO ÁREAS] idDivision=" + idDivision + "  -> " + lista.size() + " filas.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    @Override
    public boolean create(Area a) {
        // OJO: ID_AREA debe venir de secuencia/trigger si es PK NOT NULL
        String sql = "INSERT INTO AREA (ID_AREA, NOMBRE, DESCRIPCION, ID_DIVISION) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            ps.setString(2, a.getNombre());
            if (a.getIdDivision() > 0) ps.setInt(4, a.getIdDivision()); else ps.setNull(4, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Area a) {
        String sql = "UPDATE AREA SET NOMBRE=?, DESCRIPCION=?, ID_DIVISION=? WHERE ID_AREA=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            if (a.getIdDivision() > 0) ps.setInt(3, a.getIdDivision()); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean deleteById(long idArea) {
        String sql = "DELETE FROM AREA WHERE ID_AREA = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idArea);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private static Area map(ResultSet rs) throws SQLException {
        Area a = new Area();
        a.setId(rs.getInt("ID_AREA"));
        a.setNombre(rs.getString("NOMBRE"));
        a.setIdDivision(rs.getInt("ID_DIVISION"));
        a.setDivision(rs.getString("DIVISION"));
        return a;
    }
}
