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
import utez.edu.mx.prestamos_utez.model.Usuario;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

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

    private final IObjetoDao dao = new ObjetoImplDao();

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

        configurarColumnaAcciones();
        btnAgregarObjeto.setOnAction(e -> abrirFormularioCrear());

        cargarObjetos();
    }

    public void cargarObjetos() {
        Usuario u = AdministradorSesion.getUsuarioActual();
        List<Objeto> lista;

        if (u != null
                && u.getNombreRol() != null
                && u.getNombreRol().equalsIgnoreCase("Encargado")
                && u.getIdDivision() > 0) {
            lista = ((ObjetoImplDao) dao).obtenerPorDivision(u.getIdDivision());
        } else {
            lista = dao.obtenerTodos();
        }

        tablaObjetos.getItems().setAll(lista);
    }

    private void abrirFormularioCrear() {
        abrirFormulario(null, "Agregar Objeto");
    }

    private void editarObjeto(Objeto objeto) {
        if (objeto == null) return;
        abrirFormulario(objeto, "Editar Objeto");
    }

    private void abrirFormulario(Objeto objeto, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/utez/edu/mx/prestamos_utez/view/objeto_form.fxml")
            ); // <— MISMO FXML
            Parent root = loader.load();

            ObjetoFormController form = loader.getController();
            form.setObjetoListController(this);
            if (objeto != null) form.setObjetoAEditar(objeto);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario:\n" + ex.getMessage()).showAndWait();
        }
    }

    private void borrarObjeto(Objeto objeto) {
        if (objeto == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar objeto");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que deseas eliminar \"" + objeto.getNombre() + "\"?\n" +
                "Se eliminarán también sus unidades (si existen).");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = ((ObjetoImplDao) dao).deleteById(objeto.getId());
                if (ok) {
                    cargarObjetos();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "No se pudo eliminar. Revisa la consola para más detalles.").showAndWait();
                }
            }
        });
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();
            private final HBox box = new HBox(12, btnEditar, btnEliminar);

            {
                ImageView editIcon = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/editar.png").toExternalForm());
                editIcon.setFitHeight(18);
                editIcon.setFitWidth(18);
                btnEditar.setGraphic(editIcon);
                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEditar.setOnAction(e -> editarObjeto(getTableView().getItems().get(getIndex())));

                ImageView deleteIcon = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/eliminar.png").toExternalForm());
                deleteIcon.setFitHeight(18);
                deleteIcon.setFitWidth(18);
                btnEliminar.setGraphic(deleteIcon);
                btnEliminar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setOnAction(e -> borrarObjeto(getTableView().getItems().get(getIndex())));

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
