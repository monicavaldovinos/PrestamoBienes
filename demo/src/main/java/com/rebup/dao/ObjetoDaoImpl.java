package com.rebup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rebup.model.Objeto;

public class ObjetoDaoImpl implements ObjetoDao {

    private Connection conn;

    public ObjetoDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Objeto> listarTodos() {
        return obtenerObjetos("SELECT * FROM OBJETO");
    }

    @Override
    public List<Objeto> listarDisponibles() {
        return obtenerObjetos("SELECT * FROM OBJETO WHERE CANTIDAD > 0");
    }

    @Override
    public List<Objeto> listarNoDisponibles() {
        return obtenerObjetos("SELECT * FROM OBJETO WHERE CANTIDAD = 0");
    }

    @Override
    public void actualizarCantidadYEstado(int idObjeto, int cantidad) throws SQLException {
        String sql = "UPDATE OBJETO SET CANTIDAD = ?, ESTADO = ? WHERE ID_OBJETO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, cantidad > 0 ? "DISPONIBLE" : "NO DISPONIBLE");
            ps.setInt(3, idObjeto);
            ps.executeUpdate();
        }
    }

    private List<Objeto> obtenerObjetos(String sql) {
        List<Objeto> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Objeto(
                    rs.getInt("ID_OBJETO"),
                    rs.getString("NOMBRE"),
                    rs.getString("TIPO"),
                    rs.getString("NUMERO_SERIE"),
                    rs.getString("ESTADO"),
                    rs.getInt("CANTIDAD"),
                    rs.getString("IMAGEN_URL"),
                    rs.getString("DESCRIPCION"),
                    rs.getString("NUMERO_INVENTARIO")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void actualizarCantidadObjeto(int idObjeto, int cantidad) throws SQLException {
        String sql = "UPDATE OBJETO SET CANTIDAD = CANTIDAD + ? WHERE ID_OBJETO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idObjeto);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertarObjeto(Objeto obj) throws SQLException {
        String sql = "INSERT INTO OBJETO (NOMBRE, TIPO, NUMERO_SERIE, ESTADO, CANTIDAD, IMAGEN_URL, DESCRIPCION, NUMERO_INVENTARIO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipo());
            ps.setString(3, obj.getNumeroSerie());
            ps.setString(4, obj.getEstado());
            ps.setInt(5, obj.getCantidad());
            ps.setString(6, obj.getImagenUrl());
            ps.setString(7, obj.getDescripcion());
            ps.setString(8, obj.getNumeroInventario());
            ps.executeUpdate();
        }
    }
}
