package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.AreaImplDao;
import utez.edu.mx.prestamos_utez.model.Area;

import java.net.URL;
import java.util.ResourceBundle;

public class AreaListController implements Initializable {

    @FXML private TableView<Area> tblAreas;
    @FXML private TableColumn<Area, Void>   colNo;
    @FXML private TableColumn<Area, String> colNombre;
    @FXML private TableColumn<Area, String> colDivision;
    @FXML private TableColumn<Area, Void>   colAcciones;

    private final AreaImplDao dao = new AreaImplDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Datos
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colDivision.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDivision()));

        configurarColumnaNumeracion();

        configurarColumnaAcciones();

        cargarAreas();
    }

    private void configurarColumnaNumeracion() {
        colNo.setStyle("-fx-alignment: CENTER;");
        colNo.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNo.setSortable(false);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar   = crearBotonIcono("editar.png");
            private final Button btnEliminar = crearBotonIcono("eliminar.png");
            private final HBox box = new HBox(8);

            {
                btnEditar.setOnAction(e -> {
                    Area area = getTableView().getItems().get(getIndex());
                    editarArea(area);
                });
                btnEliminar.setOnAction(e -> {
                    Area area = getTableView().getItems().get(getIndex());
                    eliminarArea(area);
                });
                box.getChildren().addAll(btnEditar, btnEliminar);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        colAcciones.setSortable(false);
        colAcciones.setStyle("-fx-alignment: CENTER;");
    }

    /** Crea un botón sin fondo con un ImageView 16x16. Si no existe el recurso, regresa un botón con texto. */
    private Button crearBotonIcono(String fileName) {
        Button b = new Button();
        b.setFocusTraversable(false);
        b.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-cursor: hand;");

        String path = "/utez/edu/mx/prestamos_utez/icons/" + fileName;
        URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new Image(url.toExternalForm(), 16, 16, true, true);
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            b.setGraphic(iv);
        } else {
            System.err.println("⚠ Icono no encontrado: " + path);
            // Fallback para que no truene si el icono no está
            b.setText(fileName.contains("editar") ? "Editar" : "Eliminar");
        }
        return b;
    }

    public void cargarAreas() {
        tblAreas.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        tblAreas.refresh();
    }

    /* ===== Toolbar ===== */
    @FXML
    private void nuevaArea() { abrirFormulario(null); }

    /* ===== Acciones por fila ===== */
    private void editarArea(Area sel) {
        if (sel == null) return;
        abrirFormulario(sel);
    }

    private void eliminarArea(Area sel) {
        if (sel == null) return;
        var conf = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el área \"" + sel.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO).showAndWait();

        if (conf.isPresent() && conf.get() == ButtonType.YES) {
            if (dao.deleteById(sel.getId())) {
                cargarAreas();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "No se pudo eliminar. Revisa dependencias (objetos/ítems).",
                        ButtonType.OK).showAndWait();
            }
        }
    }

    /* ===== Modal ===== */
    private void abrirFormulario(Area aEditar) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/area_form.fxml"));
            Parent root = fx.load();
            AreaFormController ctrl = fx.getController();
            ctrl.setAreaListController(this);
            if (aEditar != null) ctrl.setAreaEditar(aEditar);

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle(aEditar == null ? "Nueva Área" : "Editar Área");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error al abrir formulario: " + e.getMessage(),
                    ButtonType.OK).showAndWait();
        }
    }
}
