package utez.edu.mx.prestamos_utez.dao;

import utez.edu.mx.prestamos_utez.model.Usuario;
import java.sql.SQLException;

public interface IUsuarioDao {
    Usuario login(String correo, String pass) throws SQLException;
}
