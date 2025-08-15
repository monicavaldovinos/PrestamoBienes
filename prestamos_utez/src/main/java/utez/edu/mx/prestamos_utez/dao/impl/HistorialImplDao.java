package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.IHistorialDao;
import utez.edu.mx.prestamos_utez.model.HistorialPrestamo;
import utez.edu.mx.prestamos_utez.model.PrestamoDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialImplDao implements IHistorialDao {

    @Override
    public List<HistorialPrestamo> listar() {
        // Resumen por préstamo (sin vista, directo a tablas)
        String sql = """
            SELECT  p.ID_PRESTAMO,
                    (d.NOMBRE || ' ' || d.APELLIDOS) AS PROFESOR,
                    COUNT(pd.ID_DETALLE)              AS CANTIDAD,
                    p.FECHA_PRESTAMO,
                    p.ESTADO
            FROM PRESTAMO p
            JOIN DOCENTE d              ON d.ID_DOCENTE = p.ID_DOCENTE
            LEFT JOIN PRESTAMO_DETALLE pd ON pd.ID_PRESTAMO = p.ID_PRESTAMO
            GROUP BY p.ID_PRESTAMO, d.NOMBRE, d.APELLIDOS, p.FECHA_PRESTAMO, p.ESTADO
            ORDER BY p.ID_PRESTAMO DESC
        """;

        List<HistorialPrestamo> out = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public List<PrestamoDetalle> detallesPorPrestamo(int idPrestamo) {
        // Cohincidir con tu tabla PRESTAMO_DETALLE (usa DESCRIPCION_OBJ, NUM_SERIE, NUM_INVENTARIO, FECHA_ENTREGA, ESTADO)
        String sql = """
            SELECT ID_DETALLE, DESCRIPCION_OBJ, NUM_SERIE, NUM_INVENTARIO, FECHA_ENTREGA, ESTADO
            FROM PRESTAMO_DETALLE
            WHERE ID_PRESTAMO = ?
            ORDER BY ID_DETALLE
        """;

        List<PrestamoDetalle> out = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrestamoDetalle d = new PrestamoDetalle();
                    d.setIdDetalle(rs.getInt("ID_DETALLE"));
                    d.setArticulo(rs.getString("DESCRIPCION_OBJ"));
                    d.setNumSerie(rs.getString("NUM_SERIE"));
                    d.setNumInventario(rs.getString("NUM_INVENTARIO"));
                    d.setFechaEntrega(rs.getDate("FECHA_ENTREGA")); // puede venir null
                    d.setEstado(rs.getString("ESTADO"));            // PENDIENTE/ENTREGADO
                    out.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public boolean marcarEntregado(int idDetalle) {
        // 1) Marca el detalle como ENTREGADO (y fecha)
        String updDet = "UPDATE PRESTAMO_DETALLE SET ESTADO='ENTREGADO', FECHA_ENTREGA=SYSDATE WHERE ID_DETALLE=?";

        // 2) Si YA NO hay detalles pendientes para ese préstamo, marca el préstamo como ENTREGADO
        String updPrestamo = """
            UPDATE PRESTAMO p
               SET p.ESTADO = 'ENTREGADO', p.FECHA_DEVOLUCION = SYSDATE
             WHERE p.ID_PRESTAMO = (
                    SELECT ID_PRESTAMO FROM PRESTAMO_DETALLE WHERE ID_DETALLE = ?
             )
               AND NOT EXISTS (
                    SELECT 1
                      FROM PRESTAMO_DETALLE pd
                     WHERE pd.ID_PRESTAMO = p.ID_PRESTAMO
                       AND UPPER(pd.ESTADO) = 'PENDIENTE'
               )
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(updDet)) {
                p1.setInt(1, idDetalle);
                if (p1.executeUpdate() == 0) { // no se afectó nada
                    con.rollback();
                    return false;
                }
            }
            try (PreparedStatement p2 = con.prepareStatement(updPrestamo)) {
                p2.setInt(1, idDetalle);
                p2.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
