package utez.edu.mx.prestamos_utez.dao;
import java.sql.SQLException;

public interface IUsuarioDao {
    boolean login(String correo, String password) throws SQLException;
}
