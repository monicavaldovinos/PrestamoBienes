package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Rol;
import utez.edu.mx.prestamos_utez.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface IUsuarioDao {
    Usuario login(String correo, String pass) throws SQLException;
    List<Rol> listarRoles() throws SQLException;
}
