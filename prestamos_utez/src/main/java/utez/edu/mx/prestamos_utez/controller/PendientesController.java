// utez.edu.mx.prestamos_utez.controller.PendientesController
package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.dao.impl.PendientesDao;
import utez.edu.mx.prestamos_utez.model.HistorialPrestamo;

import java.sql.Connection;
import java.util.List;

public class PendientesController {
    @FXML private TableView<HistorialPrestamo> tabla;
    @FXML private TableColumn<HistorialPrestamo, Number> colNo;
    @FXML private TableColumn<HistorialPrestamo, String> colProfesor, colEstado;
    @FXML private TableColumn<HistorialPrestamo, Integer> colCantidad;
    @FXML private TableColumn<HistorialPrestamo, java.sql.Date> colFecha;
    @FXML private TableColumn<HistorialPrestamo, Void> colAcciones;

    @FXML public void initialize() {
        colNo.setCellValueFactory(cd ->
                Bindings.createIntegerBinding(() -> tabla.getItems().indexOf(cd.getValue()) + 1));
        colProfesor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProfesor()));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaPrestamo()));
        configurarAcciones();
        refrescar();
    }

    @FXML public void refrescar() {
        try (Connection c = DBConnection.getConnection()) {
            var dao = new PendientesDao(c);
            List<HistorialPrestamo> data = dao.listarPendientes();
            tabla.getItems().setAll(data);
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            final Button btnVer = new Button("Ver");
            final Button btnOk = new Button("Autorizar");
            final Button btnNo = new Button("Rechazar");
            {
                btnVer.setOnAction(e -> verDetalle(getPrestamoId()));
                btnOk.setOnAction(e -> aprobar(getPrestamoId()));
                btnNo.setOnAction(e -> rechazar(getPrestamoId()));
            }
            private int getPrestamoId() {
                var h = getTableView().getItems().get(getIndex());
                return h.getIdPrestamo();
            }
            @Override protected void updateItem(Void it, boolean empty) {
                super.updateItem(it, empty);
                setGraphic(empty ? null : new HBox(6, btnVer, btnOk, btnNo));
            }
        });
    }

    private void verDetalle(int idPrestamo) {
        try (var c = DBConnection.getConnection()) {
            var dao = new PendientesDao(c);
            var dets = dao.detallesPorPrestamo(idPrestamo);
            StringBuilder sb = new StringBuilder("Objetos solicitados:\n\n");
            dets.forEach(d -> sb.append("• ").append(d.getArticulo()).append("\n"));
            info(sb.toString());
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void aprobar(int idPrestamo) {
        if (!confirm("¿Autorizar este préstamo?")) return;
        try (var c = DBConnection.getConnection()) {
            new PendientesDao(c).aprobarPrestamo(idPrestamo);
            info("Préstamo autorizado.");
            refrescar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void rechazar(int idPrestamo) {
        if (!confirm("¿Rechazar este préstamo?")) return;
        try (var c = DBConnection.getConnection()) {
            new PendientesDao(c).rechazarPrestamo(idPrestamo);
            info("Préstamo rechazado.");
            refrescar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    // helpers
    private boolean confirm(String m){
        return new Alert(Alert.AlertType.CONFIRMATION,m,ButtonType.OK,ButtonType.CANCEL)
                .showAndWait().filter(b->b==ButtonType.OK).isPresent();
    }
    private void info(String m){ new Alert(Alert.AlertType.INFORMATION,m,ButtonType.OK).showAndWait(); }
    private void error(String m){ new Alert(Alert.AlertType.ERROR,m,ButtonType.OK).showAndWait(); }
}