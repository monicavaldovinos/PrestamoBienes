package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.HistorialImplDao;
import utez.edu.mx.prestamos_utez.model.PrestamoDetalle;

public class HistorialEntregaController {
    @FXML private Label lblTitulo, lblNota;
    @FXML private Button btnEntregar, btnCerrar;

    private PrestamoDetalle det;
    private Runnable onUpdated;

    public void setDetalle(PrestamoDetalle det, Runnable onUpdated) {
        this.det = det;
        this.onUpdated = onUpdated;
        lblTitulo.setText(det.getArticulo() + " (serial: " + det.getNumSerie() + ")");
    }

    @FXML private void onEntregar() {
        boolean ok = new HistorialImplDao().marcarEntregado(det.getIdDetalle());
        if (ok && onUpdated != null) onUpdated.run();
        ((Stage) btnEntregar.getScene().getWindow()).close();
    }

    @FXML private void onCerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
