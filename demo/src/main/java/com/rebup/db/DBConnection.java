package com.rebup.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:oracle:thin:@lhm7cxpcq0n4jjyb_low"; 
    private static final String USER     = "ADMIN";
    private static final String PASSWORD = "Ebarravaldo7#";


    public static Connection getConnection() throws SQLException {
        
        System.setProperty("oracle.net.tns_admin", "C:\\Users\\danie\\Desktop\\Wallet_LHM7CXPCQ0N4JJYB");
        System.setProperty("oracle.net.ssl_server_dn_match", "true");


        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
    System.out.println("Iniciando conexión...");
    try (Connection conn = getConnection()) {
        if (conn != null && !conn.isClosed()) {
            System.out.println("¡Conexión exitosa!");
        } else {
            System.out.println("Conexión fue nula o cerrada.");
        }
    } catch (SQLException e) {
        System.out.println("Error en la conexión:");
        e.printStackTrace();
    }
    System.out.println("Fin del programa");
}
}

