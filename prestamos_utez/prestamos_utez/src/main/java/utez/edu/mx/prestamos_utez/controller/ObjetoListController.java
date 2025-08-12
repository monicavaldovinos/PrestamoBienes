package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IObjetoDao;
import utez.edu.mx.prestamos_utez.dao.impl.ObjetoImplDao;
import utez.edu.mx.prestamos_utez.model.Objeto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ObjetoListController implements Initializable {

    @FXML private TableView<Objeto> tablaObjetos;
    @FXML private TableColumn<Objeto, Number> colNo;
    @FXML private TableColumn<Objeto, String> colNombre;
    @FXML private TableColumn<Objeto, Number> colCantidad;
    @FXML private TableColumn<Objeto, String> colDivision;
    @FXML private TableColumn<Objeto, Void>   colAcciones;
    @FXML private Button btnAgregarObjeto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNo.setCellValueFactory(cd ->
                Bindings.createIntegerBinding(() ->
                        tablaObjetos.getItems().indexOf(cd.getValue()) + 1
                )
        );
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));

        cargarObjetos();
        configurarColumnaAcciones();

        btnAgregarObjeto.setOnAction(e -> abrirFormularioCrear());
    }

    public void cargarObjetos() {
        IObjetoDao dao = new ObjetoImplDao();
        var usuario = utez.edu.mx.prestamos_utez.sesion.AdministradorSesion.getUsuarioActual();
        if (usuario != null && "Encargado".equalsIgnoreCase(usuario.getNombreRol()) && usuario.getIdDivision() != 0) {
            List<Objeto> lista = ((ObjetoImplDao)dao).obtenerPorDivision(usuario.getIdDivision());
            tablaObjetos.getItems().setAll(lista);
        } else {
            List<Objeto> lista = dao.obtenerTodos();
            tablaObjetos.getItems().setAll(lista);
        }
    }

    private void abrirFormularioCrear() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/objeto_form.fxml"));
            Parent root = loader.load();

            ObjetoFormController form = loader.getController();
            form.setObjetoListController(this);  // refrescar al cerrar

            Stage stage = new Stage();
            stage.setTitle("Agregar Objeto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void editarObjeto(Objeto objeto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/objeto_form_edit.fxml"));
            Parent root = loader.load();

            ObjetoFormController form = loader.getController();
            form.setObjetoListController(this);
            form.setObjetoAEditar(objeto); // modo edición

            Stage stage = new Stage();
            stage.setTitle("Editar Objeto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final HBox box = new HBox(12, btnEditar);

            {
                ImageView editIcon = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/editar.png").toExternalForm());
                editIcon.setFitHeight(18);
                editIcon.setFitWidth(18);
                btnEditar.setGraphic(editIcon);
                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEditar.setOnAction(e -> {
                    Objeto o = getTableView().getItems().get(getIndex());
                    editarObjeto(o);
                });
                box.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }
}
