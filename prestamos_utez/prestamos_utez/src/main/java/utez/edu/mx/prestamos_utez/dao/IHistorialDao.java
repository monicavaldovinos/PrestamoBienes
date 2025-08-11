package utez.edu.mx.prestamos_utez.dao;
import java.util.List;
import utez.edu.mx.prestamos_utez.model.*;

public interface IHistorialDao {
    List<HistorialPrestamo> listar();
    List<PrestamoDetalle> detallesPorPrestamo(int idPrestamo);
    boolean marcarEntregado(int idDetalle);
}
