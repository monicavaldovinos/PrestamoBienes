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
                        "       v.ID_DIVISION, v.NOMBRE AS DIVISION, a.ID_AREA, a.NOMBRE AS AREA " +
                        "FROM DOCENTE d " +
                        "JOIN DOCENTE_DIVISION dd ON d.ID_DOCENTE = dd.ID_DOCENTE " +
                        "JOIN DIVISION v ON v.ID_DIVISION = dd.ID_DIVISION " +
                        "LEFT JOIN AREA a ON a.ID_AREA = dd.ID_AREA " +        // <- puede ser null si aún no asignan
                        "ORDER BY d.ID_DOCENTE";

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
                p.setIdDivision(rs.getInt("ID_DIVISION"));
                p.setDivision(rs.getString("DIVISION"));
                p.setIdArea(rs.getInt("ID_AREA"));          // 0 si null
                p.setArea(rs.getString("AREA"));            // puede ser null
                lista.add(p);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean create(Profesor profesor) {
        String insertDoc =
                "INSERT INTO DOCENTE (NOMBRE, APELLIDOS, CORREO, TELEFONO) VALUES (?, ?, ?, ?)";
        String curval = "SELECT SEQ_DOCENTE_ID.CURRVAL FROM DUAL";
        String insertRel =
                "INSERT INTO DOCENTE_DIVISION (ID_DOCENTE, ID_DIVISION, ID_AREA) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // DOCENTE
            try (PreparedStatement ps = con.prepareStatement(insertDoc)) {
                ps.setString(1, profesor.getNombre());
                ps.setString(2, profesor.getApellidos());
                ps.setString(3, profesor.getCorreo());
                ps.setString(4, profesor.getTelefono());
                ps.executeUpdate();
            }

            // ID generado
            int idDocente;
            try (PreparedStatement ps = con.prepareStatement(curval);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idDocente = rs.getInt(1);
            }

            // Relación división + área
            try (PreparedStatement ps = con.prepareStatement(insertRel)) {
                ps.setInt(1, idDocente);
                ps.setInt(2, profesor.getIdDivision());
                if (profesor.getIdArea() > 0) {
                    ps.setInt(3, profesor.getIdArea());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
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

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(delRel)) {
                p1.setInt(1, id);
                p1.executeUpdate();  // primero borra relaciones (evita ORA-02292)
            }

            int rowsDoc;
            try (PreparedStatement p2 = con.prepareStatement(delDoc)) {
                p2.setInt(1, id);
                rowsDoc = p2.executeUpdate();   // luego borra el docente
            }

            con.commit();
            return rowsDoc > 0; // true si realmente borró el docente
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
        }
    }


    @Override
    public boolean update(Profesor profesor) {
        String updDoc = "UPDATE DOCENTE SET NOMBRE=?, APELLIDOS=?, CORREO=?, TELEFONO=? WHERE ID_DOCENTE=?";
        String delRel = "DELETE FROM DOCENTE_DIVISION WHERE ID_DOCENTE=?";
        String insRel = "INSERT INTO DOCENTE_DIVISION (ID_DOCENTE, ID_DIVISION, ID_AREA) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // DOCENTE
            try (PreparedStatement p1 = con.prepareStatement(updDoc)) {
                p1.setString(1, profesor.getNombre());
                p1.setString(2, profesor.getApellidos());
                p1.setString(3, profesor.getCorreo());
                p1.setString(4, profesor.getTelefono());
                p1.setInt(5, profesor.getId());
                p1.executeUpdate();
            }

            // Refrescar relación
            try (PreparedStatement p2 = con.prepareStatement(delRel)) {
                p2.setInt(1, profesor.getId());
                p2.executeUpdate();
            }
            try (PreparedStatement p3 = con.prepareStatement(insRel)) {
                p3.setInt(1, profesor.getId());
                p3.setInt(2, profesor.getIdDivision());
                if (profesor.getIdArea() > 0) {
                    p3.setInt(3, profesor.getIdArea());
                } else {
                    p3.setNull(3, Types.INTEGER);
                }
                p3.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Profesor> obtenerPorDivision(int idDivision) {
        List<Profesor> lista = new ArrayList<>();
        String sql =
                "SELECT d.ID_DOCENTE, d.NOMBRE, d.APELLIDOS, d.CORREO, d.TELEFONO, " +
                        "       v.ID_DIVISION, v.NOMBRE AS DIVISION, " +
                        "       a.ID_AREA, a.NOMBRE AS AREA " +
                        "FROM DOCENTE d " +
                        "JOIN DOCENTE_DIVISION dd ON d.ID_DOCENTE = dd.ID_DOCENTE " +
                        "JOIN DIVISION v ON v.ID_DIVISION = dd.ID_DIVISION " +
                        "LEFT JOIN AREA a ON a.ID_AREA = dd.ID_AREA " +
                        "WHERE v.ID_DIVISION = ? " +
                        "ORDER BY d.ID_DOCENTE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Profesor p = new Profesor();
                    p.setId(rs.getInt("ID_DOCENTE"));
                    p.setNombre(rs.getString("NOMBRE"));
                    p.setApellidos(rs.getString("APELLIDOS"));
                    p.setCorreo(rs.getString("CORREO"));
                    p.setTelefono(rs.getString("TELEFONO"));

                    p.setIdDivision(rs.getInt("ID_DIVISION"));
                    p.setDivision(rs.getString("DIVISION"));
                    p.setIdArea(rs.getInt("ID_AREA"));     // 0 si es NULL
                    p.setArea(rs.getString("AREA"));       // puede ser null

                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


}
