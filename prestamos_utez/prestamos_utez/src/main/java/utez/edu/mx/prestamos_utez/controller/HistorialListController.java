package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.HistorialImplDao;
import utez.edu.mx.prestamos_utez.model.HistorialPrestamo;
import java.util.List;

public class HistorialListController {
    @FXML private TableView<HistorialPrestamo> tabla;
    @FXML private TableColumn<HistorialPrestamo, Number> colNo;
    @FXML private TableColumn<HistorialPrestamo, String> colProfesor, colEstado;
    @FXML private TableColumn<HistorialPrestamo, Integer> colCantidad;
    @FXML private TableColumn<HistorialPrestamo, java.sql.Date> colFecha;
    @FXML private TableColumn<HistorialPrestamo, Void> colAcciones;

    public void initialize() {
        colNo.setCellValueFactory(cd ->
                Bindings.createIntegerBinding(() -> tabla.getItems().indexOf(cd.getValue()) + 1));
        colProfesor.setCellValueFactory(c -> javafx.beans.property.SimpleStringProperty
                .stringExpression(c.getValue().getProfesor()));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaPrestamo()));

        configurarAcciones();
        cargar();
    }

    private void cargar() {
        List<HistorialPrestamo> data = new HistorialImplDao().listar();
        tabla.getItems().setAll(data);
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button();
            { // ícono de ojo
                ImageView eye = new ImageView(getClass().getResource("/utez/edu/mx/prestamos_utez/icons/eye.png").toExternalForm());
                eye.setFitWidth(18); eye.setFitHeight(18);
                btn.setGraphic(eye); btn.setStyle("-fx-background-color: transparent;");
                btn.setOnAction(e -> {
                    HistorialPrestamo h = getTableView().getItems().get(getIndex());
                    abrirDetalle(h.getIdPrestamo());
                });
            }
            @Override protected void updateItem(Void it, boolean empty) {
                super.updateItem(it, empty);
                setGraphic(empty ? null : new HBox(6, btn));
            }
        });
    }

    private void abrirDetalle(int idPrestamo) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/utez/edu/mx/prestamos_utez/view/historial_detalle.fxml"));
            Parent root = fx.load();
            HistorialDetalleController c = fx.getController();
            c.cargar(idPrestamo, this::cargar); // pasa callback para refrescar al cerrar

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Objetos prestados");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
