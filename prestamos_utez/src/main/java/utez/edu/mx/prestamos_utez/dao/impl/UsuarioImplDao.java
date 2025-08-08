package utez.edu.mx.prestamos_utez.dao.impl;


import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IUsuarioDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioImplDao implements IUsuarioDao {
    @Override
    public boolean login(String correo, String pass) throws SQLException {
        String sql="SELECT * FROM USUARIO WHERE CORREO = ? AND CONTRASENA = ?";
        try {
            Connection con = DBConnection.getConnection();//Establcer conexion
            System.out.println("Conexión OK");
            PreparedStatement ps = con.prepareStatement(sql); //Prepara la consulta para evitar inyeccion sql
            ps.setString(1,correo);
            ps.setString(2,pass);
            System.out.println("Ejecutando query:");
            System.out.println("Correo: '" + correo + "'");
            System.out.println("Password: '" + pass + "'");

            ResultSet resultSet = ps.executeQuery();//Se ejecuta la consulta

            if(resultSet.next()){
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        UsuarioImplDao dao= new UsuarioImplDao();
        try{
            System.out.println(dao.login("ana@gmail.com","1234"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

