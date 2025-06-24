package utez.edu.mx.proyectointegrador.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.converter.DefaultStringConverter;
import utez.edu.mx.proyectointegrador.model.Profesor;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class ProfesoresController implements Initializable {

    @FXML
    private Button btnAgregarProfesor;
    @FXML
    private TableView<Profesor> tablaProfesores;
    @FXML
    private TableColumn<Profesor, String> colNombre;
    @FXML
    private TableColumn<Profesor, String> colDivision;
    @FXML
    private TableColumn<Profesor, Void> colAcciones;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas
        btnAgregarProfesor.setOnAction(event -> mostrarFormularioProfesor());

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));

        // Hacer columnas editables (opcional)
        tablaProfesores.setEditable(true);
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));


        // Datos de prueba
        tablaProfesores.getItems().addAll(
                new Profesor("Luis Pérez", "Ingeniería"),
                new Profesor("Ana López", "Administración")
        );

        // Configurar botones de acción
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();
            private final HBox contenedor = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/utez/edu/mx/proyectointegrador/iconos/editar.png"))
                ));
                btnEditar.setStyle("-fx-background-color: transparent;");

                btnEliminar.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/utez/edu/mx/proyectointegrador/iconos/eliminar.png"))
                ));
                btnEliminar.setStyle("-fx-background-color: transparent;");

                btnEditar.setOnAction(e -> {
                    Profesor prof = getTableView().getItems().get(getIndex());
                    // lógica de edición aquí
                    System.out.println("Editar: " + prof.getNombre());
                });

                btnEliminar.setOnAction(e -> {
                    Profesor prof = getTableView().getItems().get(getIndex());
                    tablaProfesores.getItems().remove(prof);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
        tablaProfesores.setFixedCellSize(30);
        tablaProfesores.prefHeightProperty().bind(
                tablaProfesores.fixedCellSizeProperty()
                        .multiply(Bindings.size(tablaProfesores.getItems()).add(1.01))
        );


    }


    private void mostrarFormularioProfesor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utez/edu/mx/proyectointegrador/FormularioProfesor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar Profesor");
            stage.setScene(new Scene(root, 500, 450));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
