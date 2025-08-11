package utez.edu.mx.prestamos_utez.dao.impl;


import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IUsuarioDao;
import utez.edu.mx.prestamos_utez.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioImplDao implements IUsuarioDao {
    @Override
    public Usuario login(String correo, String pass) throws SQLException {
        String sql = """
            SELECT ID_USUARIO, NOMBRE, APELLIDOS, CORREO, ID_ROL
            FROM USUARIO
            WHERE CORREO = ? AND CONTRASENA = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexión OK");
            System.out.println("Ejecutando query:");
            System.out.println("Correo: '" + correo + "'");
            System.out.println("Password: '" + pass + "'");

            ps.setString(1, correo);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("ID_USUARIO"));
                    u.setNombre(rs.getString("NOMBRE"));
                    u.setApellidos(rs.getString("APELLIDOS"));
                    u.setCorreo(rs.getString("CORREO"));
                    u.setIdRol(rs.getString("ID_ROL"));
                    return u; // éxito
                }
                return null; // credenciales inválidas
            }
        }
    }
    }


