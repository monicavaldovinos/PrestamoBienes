package utez.edu.mx.prestamos_utez.dao.impl;

import utez.edu.mx.prestamos_utez.model.HistorialPrestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDao {
    private final Connection conn;

    public PrestamoDao(Connection conn) {
        this.conn = conn;
    }

    public List<HistorialPrestamo> listarPendientes() throws SQLException {
        String q = """
        SELECT p.ID_PRESTAMO,
               TRIM(COALESCE(d.NOMBRE,'') || ' ' || COALESCE(d.APELLIDOS,'')) AS PROFESOR,
               (SELECT COALESCE(SUM(CANTIDAD),0)
                  FROM PRESTAMO_DETALLE pd
                 WHERE pd.ID_PRESTAMO = p.ID_PRESTAMO) AS CANTIDAD,
               CAST(p.FECHA_PRESTAMO AS DATE) AS FECHA_PRESTAMO,
               p.ESTADO
          FROM PRESTAMO p
          JOIN DOCENTE d ON d.ID_DOCENTE = p.ID_DOCENTE
         WHERE p.ESTADO = 'PENDIENTE'
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
}