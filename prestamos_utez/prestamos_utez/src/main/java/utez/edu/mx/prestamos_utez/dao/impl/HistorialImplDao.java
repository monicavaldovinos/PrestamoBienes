package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IHistorialDao;
import utez.edu.mx.prestamos_utez.model.*;
import java.sql.*;
import java.util.*;

public class HistorialImplDao implements IHistorialDao {

    @Override
    public List<HistorialPrestamo> listar() {
        String sql = "SELECT ID_PRESTAMO, PROFESOR, CANTIDAD, FECHA_PRESTAMO, ESTADO " +
                "FROM VW_PRESTAMO_RESUMEN ORDER BY ID_PRESTAMO DESC";
        List<HistorialPrestamo> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HistorialPrestamo h = new HistorialPrestamo();
                h.setIdPrestamo(rs.getInt("ID_PRESTAMO"));
                h.setProfesor(rs.getString("PROFESOR"));
                h.setCantidad(rs.getInt("CANTIDAD"));
                h.setFechaPrestamo(rs.getDate("FECHA_PRESTAMO"));
                h.setEstado(rs.getString("ESTADO"));
                out.add(h);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }

    @Override
    public List<PrestamoDetalle> detallesPorPrestamo(int idPrestamo) {
        String sql = "SELECT pd.ID_DETALLE, a.NOMBRE ARTICULO, " +
                "COALESCE(pd.NUM_SERIE, a.NUMERO_SERIE) NUM_SERIE, " +
                "pd.NUM_INVENTARIO, pd.FECHA_ENTREGA, pd.ESTADO " +
                "FROM PRESTAMO_DETALLE pd JOIN ARTICULO a ON a.ID_ARTICULO=pd.ID_ARTICULO " +
                "WHERE pd.ID_PRESTAMO = ? ORDER BY pd.ID_DETALLE";
        List<PrestamoDetalle> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrestamoDetalle d = new PrestamoDetalle();
                    d.setIdDetalle(rs.getInt("ID_DETALLE"));
                    d.setArticulo(rs.getString("ARTICULO"));
                    d.setNumSerie(rs.getString("NUM_SERIE"));
                    d.setNumInventario(rs.getString("NUM_INVENTARIO"));
                    d.setFechaEntrega(rs.getDate("FECHA_ENTREGA"));
                    d.setEstado(rs.getString("ESTADO"));
                    out.add(d);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }

    @Override
    public boolean marcarEntregado(int idDetalle) {
        String sql = "UPDATE PRESTAMO_DETALLE SET ESTADO='ENTREGADO', FECHA_ENTREGA=SYSDATE WHERE ID_DETALLE=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDetalle);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
