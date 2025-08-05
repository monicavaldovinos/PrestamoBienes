package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.model.Profesor;
import utez.edu.mx.prestamos_utez.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesorImplDao implements IProfesorDao {

    @Override
    public List<Profesor> obtenerTodos() {
        List<Profesor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM docentes";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Profesor p = new Profesor();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDivision(rs.getString("division"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean create(Profesor profesor) {
        String sql = "INSERT INTO docentes(nombre, division) VALUES (?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, profesor.getNombre());
            ps.setString(2, profesor.getDivision());
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
