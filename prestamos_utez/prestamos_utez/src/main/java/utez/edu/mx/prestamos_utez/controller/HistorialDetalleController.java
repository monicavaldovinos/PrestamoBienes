package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.HistorialImplDao;
import utez.edu.mx.prestamos_utez.model.PrestamoDetalle;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HistorialDetalleController {
    @FXML private TableView<PrestamoDetalle> tabla;
    @FXML private TableColumn<PrestamoDetalle, String> colObj, colEstado;
    @FXML private TableColumn<PrestamoDetalle, String> colSerie, colInv;
    @FXML private TableColumn<PrestamoDetalle, java.sql.Date> colEntrega;
    @FXML private TableColumn<PrestamoDetalle, Void> colAcciones;

    private int idPrestamo;
    private Runnable onCloseRefresh;


    @FXML private void onCerrar() {
        ((Stage) tabla.getScene().getWindow()).close();
    }
    public void cargar(int idPrestamo, Runnable onCloseRefresh) {
        this.idPrestamo = idPrestamo;
        this.onCloseRefresh = onCloseRefresh;

        configurarColumnas();   // <- IMPORTANTE: mapear columnas
        configurarAcciones();
        cargarTabla();
    }
    private void configurarColumnas() {
        // Deben coincidir con los getters de PrestamoDetalle
        colObj.setCellValueFactory(new PropertyValueFactory<>("articulo"));
        colSerie.setCellValueFactory(new PropertyValueFactory<>("numSerie"));
        colInv.setCellValueFactory(new PropertyValueFactory<>("numInventario"));
        colEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // (Opcional) formato de fecha yyyy-MM-dd (si viene null, mostrar vacío)
        colEntrega.setCellFactory(col -> new TableCell<PrestamoDetalle, java.sql.Date>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override protected void updateItem(java.sql.Date d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "" : d.toLocalDate().format(fmt));
            }
        });
    }

    private void cargarTabla() {
        List<PrestamoDetalle> data = new HistorialImplDao().detallesPorPrestamo(idPrestamo);
        tabla.getItems().setAll(data);
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Entregar");
            {
                btn.setOnAction(e -> {
                    PrestamoDetalle d = getTableView().getItems().get(getIndex());
                    abrirEntregar(d);
                });
            }
            @Override protected void updateItem(Void it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                PrestamoDetalle d = getTableView().getItems().get(getIndex());
                btn.setDisable(!"PENDIENTE".equalsIgnoreCase(d.getEstado()));
                setGraphic(btn);
            }
        });
    }

    private void abrirEntregar(PrestamoDetalle det) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/utez/edu/mx/prestamos_utez/view/historial_entrega.fxml"));
            Parent root = fx.load();
            HistorialEntregaController c = fx.getController();
            c.setDetalle(det, () -> { cargarTabla(); if (onCloseRefresh!=null) onCloseRefresh.run(); });

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Entregar objeto");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
