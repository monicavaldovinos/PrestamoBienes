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
        String sql =
                "SELECT d.ID_DOCENTE, d.NOMBRE, d.APELLIDOS, d.CORREO, d.TELEFONO, " +
                        "       v.NOMBRE AS DIVISION " +
                        "FROM DOCENTE d " +
                        "JOIN DOCENTE_DIVISION dd ON d.ID_DOCENTE = dd.ID_DOCENTE " +
                        "JOIN DIVISION v ON v.ID_DIVISION = dd.ID_DIVISION";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Profesor p = new Profesor();
                p.setId(rs.getInt("ID_DOCENTE"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellidos(rs.getString("APELLIDOS"));
                p.setCorreo(rs.getString("CORREO"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setDivision(rs.getString("DIVISION"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public boolean create(Profesor profesor) {
        String insertDoc =
                "INSERT INTO DOCENTE (NOMBRE, APELLIDOS, CORREO, TELEFONO) VALUES (?, ?, ?, ?)";
        String curval =
                "SELECT SEQ_DOCENTE_ID.CURRVAL FROM DUAL";
        String insertRel =
                "INSERT INTO DOCENTE_DIVISION (ID_DOCENTE, ID_DIVISION) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // 1) Inserta DOCENTE (sin ID)
            try (PreparedStatement ps = con.prepareStatement(insertDoc)) {
                ps.setString(1, profesor.getNombre());
                ps.setString(2, profesor.getApellidos());
                ps.setString(3, profesor.getCorreo());
                ps.setString(4, profesor.getTelefono());
                ps.executeUpdate();
            }

            // 2) Recupera el ID generado por el trigger/secuencia
            int idDocente;
            try (PreparedStatement ps = con.prepareStatement(curval);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idDocente = rs.getInt(1);
            }

            // 3) Inserta la relación con la división (si aplica)
            try (PreparedStatement ps = con.prepareStatement(insertRel)) {
                ps.setInt(1, idDocente);
                ps.setInt(2, profesor.getIdDivision());  // asegúrate de pasar un ID válido
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean deleteById(int id) {
        String delRel = "DELETE FROM DOCENTE_DIVISION WHERE ID_DOCENTE = ?";
        String delDoc = "DELETE FROM DOCENTE WHERE ID_DOCENTE = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(delRel);
                 PreparedStatement p2 = con.prepareStatement(delDoc)) {

                // Primero borrar en tabla intermedia
                p1.setInt(1, id);
                p1.executeUpdate();

                // Luego borrar el docente
                p2.setInt(1, id);
                p2.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean update(Profesor profesor) {
        String updDoc = "UPDATE DOCENTE SET NOMBRE=?, APELLIDOS=?, CORREO=?, TELEFONO=? WHERE ID_DOCENTE=?";
        String delRel = "DELETE FROM DOCENTE_DIVISION WHERE ID_DOCENTE=?";
        String insRel = "INSERT INTO DOCENTE_DIVISION (ID_DOCENTE, ID_DIVISION) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(updDoc)) {
                p1.setString(1, profesor.getNombre());
                p1.setString(2, profesor.getApellidos());
                p1.setString(3, profesor.getCorreo());
                p1.setString(4, profesor.getTelefono());
                p1.setInt(5, profesor.getId());
                p1.executeUpdate();
            }

            // Si también puede cambiar de división, refrescamos la relación
            try (PreparedStatement p2 = con.prepareStatement(delRel)) {
                p2.setInt(1, profesor.getId());
                p2.executeUpdate();
            }
            try (PreparedStatement p3 = con.prepareStatement(insRel)) {
                p3.setInt(1, profesor.getId());
                p3.setInt(2, profesor.getIdDivision());
                p3.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
