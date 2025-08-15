package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IObjetoDao;
import utez.edu.mx.prestamos_utez.model.Objeto;
import utez.edu.mx.prestamos_utez.model.ObjetoItem;
import utez.edu.mx.prestamos_utez.model.ObjetoArea;   // <-- IMPORTANTE

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoImplDao implements IObjetoDao {

    /* ====================== SELECT BASE ====================== */
    private static final String SEL_BASE = """
        SELECT o.ID_OBJETO, o.NOMBRE, o.NUMERO_INVENTARIO, o.NUMERO_SERIE,
               o.DESCRIPCION, o.IMAGEN_URL, o.CANTIDAD, d.NOMBRE AS DIVISION
          FROM OBJETO o
          LEFT JOIN OBJETO_DIVISION od ON o.ID_OBJETO = od.ID_OBJETO
          LEFT JOIN DIVISION d        ON d.ID_DIVISION = od.ID_DIVISION
    """;

    /* ====================== LISTAR ====================== */
    @Override
    public List<Objeto> obtenerTodos() {
        List<Objeto> lista = new ArrayList<>();
        String sql = SEL_BASE + " ORDER BY o.ID_OBJETO";
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

    /* ====================== BORRAR (con hijos) ====================== */
    @Override
    public boolean deleteById(long idObjeto) {
        String delItems = "DELETE FROM OBJETO_ITEM WHERE ID_OBJETO = ?";
        String delArea  = "DELETE FROM OBJETO_AREA WHERE ID_OBJETO = ?";
        String delRel   = "DELETE FROM OBJETO_DIVISION WHERE ID_OBJETO = ?";
        String delObj   = "DELETE FROM OBJETO WHERE ID_OBJETO = ?";

        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(delItems)) {
                ps.setLong(1, idObjeto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement(delArea)) {
                ps.setLong(1, idObjeto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement(delRel)) {
                ps.setLong(1, idObjeto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement(delObj)) {
                ps.setLong(1, idObjeto);
                ps.executeUpdate();
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // si quieres, aquí puedes hacer rollback explícito en un catch separado
        }
    }

    /* ====================== CREAR (overloads) ====================== */
    @Override
    public boolean create(Objeto objeto) {
        return create(objeto, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public boolean create(Objeto objeto, List<ObjetoItem> items) {
        return create(objeto, items, new ArrayList<>());
    }

    @Override
    public boolean create(Objeto objeto, List<ObjetoItem> items, List<ObjetoArea> distribucionAreas) {
        String insertObj = """
            INSERT INTO OBJETO (NOMBRE, NUMERO_INVENTARIO, NUMERO_SERIE, DESCRIPCION, IMAGEN_URL, CANTIDAD)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String curvalObj   = "SELECT SEQ_OBJETO_ID.CURRVAL FROM DUAL";
        String insertRel   = "INSERT INTO OBJETO_DIVISION (ID_OBJETO, ID_DIVISION) VALUES (?, ?)";
        String insertItem  = """
            INSERT INTO OBJETO_ITEM (ID_ITEM, ID_OBJETO, NUMERO_SERIE, NUMERO_INVENTARIO, ESTADO, ID_AREA)
            VALUES (SEQ_OBJETO_ITEM.NEXTVAL, ?, ?, ?, 'DISPONIBLE', ?)
        """;
        String insertOA    = "INSERT INTO OBJETO_AREA (ID_OBJETO, ID_AREA, CANTIDAD) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // 1) objeto
            try (PreparedStatement ps = con.prepareStatement(insertObj)) {
                ps.setString(1, objeto.getNombre());
                ps.setString(2, objeto.getNumeroInventario());
                ps.setString(3, objeto.getNumeroSerie());
                ps.setString(4, objeto.getDescripcion());
                ps.setString(5, objeto.getImagenUrl());
                ps.setInt(6, objeto.getCantidad());
                ps.executeUpdate();
            }

            // 2) id
            int idObjeto;
            try (PreparedStatement ps = con.prepareStatement(curvalObj);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idObjeto = rs.getInt(1);
            }

            // 3) división
            try (PreparedStatement ps = con.prepareStatement(insertRel)) {
                ps.setInt(1, idObjeto);
                ps.setInt(2, objeto.getIdDivision());
                ps.executeUpdate();
            }

            // 4A) por pieza
            if (items != null && !items.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                    for (ObjetoItem it : items) {
                        ps.setInt(1, idObjeto);
                        ps.setString(2, it.getNumeroSerie());
                        ps.setString(3, it.getNumeroInventario());
                        if (it.getIdArea() == null) ps.setNull(4, Types.INTEGER);
                        else ps.setInt(4, it.getIdArea());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // 4B) por lote (solo si no hubo items)
            if ((items == null || items.isEmpty())
                    && distribucionAreas != null && !distribucionAreas.isEmpty()) {

                int suma = 0;
                for (ObjetoArea oa : distribucionAreas)
                    suma += Math.max(0, oa.getCantidad());
                if (suma != objeto.getCantidad()) {
                    con.rollback();
                    throw new SQLException("La suma por áreas (" + suma +
                            ") debe igualar la cantidad del objeto (" + objeto.getCantidad() + ").");
                }

                try (PreparedStatement ps = con.prepareStatement(insertOA)) {
                    for (ObjetoArea oa : distribucionAreas) {
                        ps.setInt(1, idObjeto);
                        ps.setInt(2, oa.getIdArea());
                        ps.setInt(3, oa.getCantidad());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ====================== UPDATE (cabecera + división) ====================== */
    @Override
    public boolean update(Objeto objeto) {
        String updObj = """
            UPDATE OBJETO
               SET NOMBRE=?, NUMERO_INVENTARIO=?, NUMERO_SERIE=?,
                   DESCRIPCION=?, IMAGEN_URL=?, CANTIDAD=?
             WHERE ID_OBJETO=?
        """;
        String delRel = "DELETE FROM OBJETO_DIVISION WHERE ID_OBJETO=?";
        String insRel = "INSERT INTO OBJETO_DIVISION (ID_OBJETO, ID_DIVISION) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

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

    /* ====================== LISTAR POR DIVISIÓN ====================== */
    @Override
    public List<Objeto> obtenerPorDivision(int idDivision) {
        List<Objeto> lista = new ArrayList<>();
        String sql = SEL_BASE + " WHERE d.ID_DIVISION = ? ORDER BY o.ID_OBJETO";
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
