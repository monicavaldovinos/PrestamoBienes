package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.model.Division;

public class DivisionFormController {

    @FXML private TextField txtNombre;

    private final DivisionImpDao dao = new DivisionImpDao();
    private DivisionListController listController;
    private Division divisionEditar; // null = crear

    void setDivisionListController(DivisionListController c) { this.listController = c; }

    void setDivisionEditar(Division d) {
        this.divisionEditar = d;
        if (d != null) txtNombre.setText(d.getNombre());
    }

    @FXML
    private void onGuardar() {
        String nombre = safe(txtNombre.getText());
        if (nombre.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "El nombre es obligatorio.", ButtonType.OK).showAndWait();
            return;
        }

        boolean ok;
        if (divisionEditar == null) {
            Division d = new Division();
            d.setNombre(nombre);
            ok = dao.create(d);
        } else {
            divisionEditar.setNombre(nombre);
            ok = dao.update(divisionEditar);
        }

        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "No se pudo guardar.", ButtonType.OK).showAndWait();
            return;
        }

        if (listController != null) listController.cargarDivisiones();
        cerrar();
    }

    @FXML
    private void onCancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}
