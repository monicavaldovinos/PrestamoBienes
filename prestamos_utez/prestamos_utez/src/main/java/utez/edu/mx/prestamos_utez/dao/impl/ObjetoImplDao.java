package utez.edu.mx.prestamos_utez.dao.impl;


import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IObjetoDao;
import utez.edu.mx.prestamos_utez.model.Objeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoImplDao implements IObjetoDao {

    @Override
    public List<Objeto> obtenerTodos() {
        List<Objeto> lista = new ArrayList<>();
        String sql =
                "SELECT o.ID_OBJETO, o.NOMBRE, o.NUMERO_INVENTARIO, o.NUMERO_SERIE, " +
                        "       o.DESCRIPCION, o.IMAGEN_URL, o.CANTIDAD, d.NOMBRE AS DIVISION " +
                        "FROM OBJETO o " +
                        "LEFT JOIN OBJETO_DIVISION od ON o.ID_OBJETO = od.ID_OBJETO " +
                        "LEFT JOIN DIVISION d ON d.ID_DIVISION = od.ID_DIVISION " +
                        "ORDER BY o.ID_OBJETO";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Objeto obj = new Objeto();
                obj.setId(rs.getInt("ID_OBJETO"));
                obj.setNombre(rs.getString("NOMBRE"));
                obj.setNumeroInventario(rs.getString("NUMERO_INVENTARIO"));
                obj.setNumeroSerie(rs.getString("NUMERO_SERIE"));
                obj.setDescripcion(rs.getString("DESCRIPCION"));
                obj.setImagenUrl(rs.getString("IMAGEN_URL"));
                obj.setCantidad(rs.getInt("CANTIDAD"));
                obj.setDivision(rs.getString("DIVISION"));
                lista.add(obj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean create(Objeto objeto) {
        String insertObj =
                "INSERT INTO OBJETO (NOMBRE, NUMERO_INVENTARIO, NUMERO_SERIE, DESCRIPCION, IMAGEN_URL, CANTIDAD) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        String curval =
                "SELECT SEQ_OBJETO_ID.CURRVAL FROM DUAL";
        String insertRel =
                "INSERT INTO OBJETO_DIVISION (ID_OBJETO, ID_DIVISION) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // 1) Insertar OBJETO
            try (PreparedStatement ps = con.prepareStatement(insertObj)) {
                ps.setString(1, objeto.getNombre());
                ps.setString(2, objeto.getNumeroInventario());
                ps.setString(3, objeto.getNumeroSerie());
                ps.setString(4, objeto.getDescripcion());
                ps.setString(5, objeto.getImagenUrl());
                ps.setInt(6, objeto.getCantidad());
                ps.executeUpdate();
            }

            // 2) Recuperar el ID generado
            int idObjeto;
            try (PreparedStatement ps = con.prepareStatement(curval);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idObjeto = rs.getInt(1);
            }

            // 3) Insertar la relación con la división
            try (PreparedStatement ps = con.prepareStatement(insertRel)) {
                ps.setInt(1, idObjeto);
                ps.setInt(2, objeto.getIdDivision());
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
    public boolean update(Objeto objeto) {
        String updObj =
                "UPDATE OBJETO SET NOMBRE=?, NUMERO_INVENTARIO=?, NUMERO_SERIE=?, " +
                        "DESCRIPCION=?, IMAGEN_URL=?, CANTIDAD=? WHERE ID_OBJETO=?";
        String delRel =
                "DELETE FROM OBJETO_DIVISION WHERE ID_OBJETO=?";
        String insRel =
                "INSERT INTO OBJETO_DIVISION (ID_OBJETO, ID_DIVISION) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // Actualizar objeto
            try (PreparedStatement p1 = con.prepareStatement(updObj)) {
                p1.setString(1, objeto.getNombre());
                p1.setString(2, objeto.getNumeroInventario());
                p1.setString(3, objeto.getNumeroSerie());
                p1.setString(4, objeto.getDescripcion());
                p1.setString(5, objeto.getImagenUrl());
                p1.setInt(6, objeto.getCantidad());
                p1.setInt(7, objeto.getId());
                p1.executeUpdate();
            }

            // Refrescar relación con la división
            try (PreparedStatement p2 = con.prepareStatement(delRel)) {
                p2.setInt(1, objeto.getId());
                p2.executeUpdate();
            }
            try (PreparedStatement p3 = con.prepareStatement(insRel)) {
                p3.setInt(1, objeto.getId());
                p3.setInt(2, objeto.getIdDivision());
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
    public List<Objeto> obtenerPorDivision(int idDivision) {
        List<Objeto> lista = new ArrayList<>();
        String sql =
                "SELECT o.ID_OBJETO, o.NOMBRE, o.NUMERO_INVENTARIO, o.NUMERO_SERIE, " +
                "       o.DESCRIPCION, o.IMAGEN_URL, o.CANTIDAD, d.NOMBRE AS DIVISION " +
                "FROM OBJETO o " +
                "LEFT JOIN OBJETO_DIVISION od ON o.ID_OBJETO = od.ID_OBJETO " +
                "LEFT JOIN DIVISION d ON d.ID_DIVISION = od.ID_DIVISION " +
                "WHERE d.ID_DIVISION = ? " +
                "ORDER BY o.ID_OBJETO";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Objeto obj = new Objeto();
                    obj.setId(rs.getInt("ID_OBJETO"));
                    obj.setNombre(rs.getString("NOMBRE"));
                    obj.setNumeroInventario(rs.getString("NUMERO_INVENTARIO"));
                    obj.setNumeroSerie(rs.getString("NUMERO_SERIE"));
                    obj.setDescripcion(rs.getString("DESCRIPCION"));
                    obj.setImagenUrl(rs.getString("IMAGEN_URL"));
                    obj.setCantidad(rs.getInt("CANTIDAD"));
                    obj.setDivision(rs.getString("DIVISION"));
                    lista.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
