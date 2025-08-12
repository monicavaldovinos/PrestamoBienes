package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IUsuarioDao;
import utez.edu.mx.prestamos_utez.model.Rol;
import utez.edu.mx.prestamos_utez.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioImplDao implements IUsuarioDao {

    @Override
    public Usuario login(String correo, String pass) throws SQLException {
        String sql = """
            SELECT u.ID_USUARIO, u.NOMBRE, u.APELLIDOS, u.CORREO, u.ID_ROL, r.NOMBRE_ROL, d.ID_DIVISION, d.NOMBRE AS NOMBRE_DIVISION
            FROM USUARIO u
            JOIN ROL r ON u.ID_ROL = r.ID_ROL
            LEFT JOIN DIVISION d ON u.ID_DIVISION = d.ID_DIVISION
            WHERE u.CORREO = ? AND u.CONTRASENA = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("ID_USUARIO"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setApellidos(rs.getString("APELLIDOS"));
                u.setCorreo(rs.getString("CORREO"));
                u.setIdRol(rs.getInt("ID_ROL"));
                u.setNombreRol(rs.getString("NOMBRE_ROL"));
                u.setIdDivision(rs.getInt("ID_DIVISION"));
                u.setNombreDivision(rs.getString("NOMBRE_DIVISION"));
                return u;
            }
        }
    }

    @Override
    public List<Rol> listarRoles() throws SQLException {
        String sql = "SELECT ID_ROL, NOMBRE_ROL FROM ROL ORDER BY ID_ROL";
        List<Rol> roles = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("ID_ROL"),
                        rs.getString("NOMBRE_ROL")
                ));
            }
        }
        return roles;
    }
}
