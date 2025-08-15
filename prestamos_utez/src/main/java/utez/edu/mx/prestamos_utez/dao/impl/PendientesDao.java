// utez.edu.mx.prestamos_utez.dao.impl.PendientesDao
package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.model.HistorialPrestamo;
import utez.edu.mx.prestamos_utez.model.PrestamoDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PendientesDao {
    private final Connection conn;
    public PendientesDao(Connection conn) { this.conn = conn; }

    // Encabezados pendientes
    public List<HistorialPrestamo> listarPendientes() throws SQLException {
        String q = """
            SELECT p.ID_PRESTAMO,
                   TRIM(COALESCE(d.NOMBRE,'')||' '||COALESCE(d.APELLIDOS,'')) AS PROFESOR,
                   (SELECT COALESCE(SUM(CANTIDAD),0)
                      FROM PRESTAMO_DETALLE pd
                     WHERE pd.ID_PRESTAMO=p.ID_PRESTAMO) AS CANTIDAD,
                   CAST(p.FECHA_PRESTAMO AS DATE) AS FECHA_PRESTAMO,
                   p.ESTADO
              FROM PRESTAMO p
              JOIN DOCENTE d ON d.ID_DOCENTE=p.ID_DOCENTE
             WHERE p.ESTADO='PENDIENTE'
             ORDER BY p.FECHA_PRESTAMO DESC
        """;
        List<HistorialPrestamo> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new HistorialPrestamo(
                        rs.getInt("ID_PRESTAMO"),
                        rs.getString("PROFESOR"),
                        rs.getInt("CANTIDAD"),
                        rs.getDate("FECHA_PRESTAMO"),
                        rs.getString("ESTADO")
                ));
            }
        }
        return out;
    }

    // Detalle de un préstamo
    public List<PrestamoDetalle> detallesPorPrestamo(int idPrestamo) throws SQLException {
        String q = """
            SELECT pd.ID_DETALLE,
                   COALESCE(o.NOMBRE, pd.DESCRIPCION_OBJ) AS ARTICULO,
                   pd.NUM_SERIE,
                   pd.NUM_INVENTARIO,
                   CAST(pd.FECHA_ENTREGA AS DATE) AS FECHA_ENTREGA,
                   pd.ESTADO
              FROM PRESTAMO_DETALLE pd
              LEFT JOIN OBJETO o ON o.ID_OBJETO=pd.ID_OBJETO
             WHERE pd.ID_PRESTAMO=?
             ORDER BY pd.ID_DETALLE
        """;
        List<PrestamoDetalle> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(q)) {
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
        }
        return out;
    }

    // Aprobar: baja stock por ID_OBJETO y cambia estados
    public void aprobarPrestamo(int idPrestamo) throws SQLException {
        String qSel = """
            SELECT ID_OBJETO, CANTIDAD
              FROM PRESTAMO_DETALLE
             WHERE ID_PRESTAMO=? AND ESTADO='PENDIENTE'
        """;
        String qBaja   = "UPDATE OBJETO SET CANTIDAD = CANTIDAD - ? WHERE ID_OBJETO=?";
        String qDetUpd = "UPDATE PRESTAMO_DETALLE SET ESTADO='PRESTADO', FECHA_ENTREGA=SYSTIMESTAMP " +
                "WHERE ID_PRESTAMO=? AND ESTADO='PENDIENTE'";
        String qPreUpd = "UPDATE PRESTAMO SET ESTADO='APROBADO' WHERE ID_PRESTAMO=?";

        boolean old = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (PreparedStatement psSel = conn.prepareStatement(qSel);
             PreparedStatement psBaja = conn.prepareStatement(qBaja);
             PreparedStatement psDU = conn.prepareStatement(qDetUpd);
             PreparedStatement psPU = conn.prepareStatement(qPreUpd)) {

            psSel.setInt(1, idPrestamo);
            try (ResultSet rs = psSel.executeQuery()) {
                while (rs.next()) {
                    int idObj = rs.getInt(1);
                    int cant  = rs.getInt(2);
                    psBaja.setInt(1, cant);
                    psBaja.setInt(2, idObj);
                    psBaja.addBatch();
                }
            }
            psBaja.executeBatch();

            psDU.setInt(1, idPrestamo);
            psDU.executeUpdate();

            psPU.setInt(1, idPrestamo);
            psPU.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback(); throw ex;
        } finally { conn.setAutoCommit(old); }
    }

    public void rechazarPrestamo(int idPrestamo) throws SQLException {
        String q1 = "UPDATE PRESTAMO_DETALLE SET ESTADO='RECHAZADO' WHERE ID_PRESTAMO=?";
        String q2 = "UPDATE PRESTAMO SET ESTADO='RECHAZADO' WHERE ID_PRESTAMO=?";
        try (PreparedStatement a = conn.prepareStatement(q1);
             PreparedStatement b = conn.prepareStatement(q2)) {
            a.setInt(1, idPrestamo); a.executeUpdate();
            b.setInt(1, idPrestamo); b.executeUpdate();
        }
    }
}