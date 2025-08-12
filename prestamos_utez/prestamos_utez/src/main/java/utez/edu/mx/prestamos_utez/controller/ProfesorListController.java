package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.model.Profesor;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfesorListController implements Initializable {

    @FXML private TableView<Profesor> tablaProfesores;
    @FXML private TableColumn<Profesor, Number> colNo;
    @FXML private TableColumn<Profesor, String> colNombre;
    @FXML private TableColumn<Profesor, String> colApellidos;
    @FXML private TableColumn<Profesor, String> colDivision;
    @FXML private TableColumn<Profesor, Void> colAcciones;
    @FXML private Button btnAgregarProfesor;
    @FXML private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNo.setCellValueFactory(cd ->
                Bindings.createIntegerBinding(() ->
                        tablaProfesores.getItems().indexOf(cd.getValue()) + 1
                )
        );
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));

        cargarProfesores();
        configurarColumnaAcciones();

        btnAgregarProfesor.setOnAction(e -> abrirFormularioCrear());
    }
    @FXML
    private void abrirHistorial() {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/historial_list.fxml"));
            Parent root = fx.load();

            Stage st = new Stage();
            st.setTitle("Historial de préstamos");
            st.setScene(new Scene(root));
            st.setMaximized(true); // si quieres pantalla completa
            st.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void onCerrarSesion(javafx.event.ActionEvent e) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/cerrar_sesion.fxml"));
            Parent root = fx.load();

            CerrarSesionController ctrl = fx.getController();

            Stage owner  = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Cerrar sesión");
            dialog.setScene(new Scene(root));

            ctrl.configure(owner, dialog);   // pásale owner y dialog

            dialog.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace(); // si hay error cargando el FXML, lo verás aquí
        }
    }


    public void cargarProfesores() {
        IProfesorDao dao = new ProfesorImplDao();
        var usuario = AdministradorSesion.getUsuarioActual();
        if (usuario != null && "Encargado".equalsIgnoreCase(usuario.getNombreRol()) && usuario.getNombreDivision() != null) {
            // Filtrar por división del encargado
            // Aquí necesitas el id de la división, ajusta si tienes el id
            // Si solo tienes el nombre, deberías obtener el id por nombre
            // Supongamos que tienes el id en usuario.getIdDivision()
            List<Profesor> lista = ((ProfesorImplDao)dao).obtenerPorDivision(usuario.getIdDivision());
            tablaProfesores.getItems().setAll(lista);
        } else {
            List<Profesor> lista = dao.obtenerTodos();
            tablaProfesores.getItems().setAll(lista);
        }
    }

    private void abrirFormularioCrear() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/profesor_form.fxml"));
            Parent root = loader.load();

            ProfesorFormController form = loader.getController();
            form.setProfesorListController(this);  // para refrescar al cerrar

            Stage stage = new Stage();
            stage.setTitle("Agregar Profesor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void editarProfesor(Profesor profesor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/profesor_form.fxml"));
            Parent root = loader.load();

            ProfesorFormController form = loader.getController();
            form.setProfesorListController(this);
            form.setProfesorAEditar(profesor); // <<< modo edición

            Stage stage = new Stage();
            stage.setTitle("Editar Profesor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void borrarProfesor(Profesor profesor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar profesor");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que deseas eliminar a " + profesor.getNombre() + "?");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                IProfesorDao dao = new ProfesorImplDao();
                boolean ok = dao.deleteById(profesor.getId());
                if (ok) {
                    cargarProfesores();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "No se pudo eliminar.").showAndWait();
                }
            }
        });
    }



    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnBorrar = new Button();
            private final HBox box = new HBox(12, btnEditar, btnBorrar);

            {
                // OJO con la ruta: los iconos deben existir en resources/utez/edu/mx/prestamos_utez/icons/
                ImageView editIcon = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/editar.png").toExternalForm());
                editIcon.setFitHeight(18);
                editIcon.setFitWidth(18);
                btnEditar.setGraphic(editIcon);
                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEditar.setOnAction(e -> {
                    Profesor p = getTableView().getItems().get(getIndex());
                    editarProfesor(p);
                });

                ImageView deleteIcon = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/eliminar.png").toExternalForm());
                deleteIcon.setFitHeight(18);
                deleteIcon.setFitWidth(18);
                btnBorrar.setGraphic(deleteIcon);
                btnBorrar.setStyle("-fx-background-color: transparent;");
                btnBorrar.setOnAction(e -> {
                    Profesor p = getTableView().getItems().get(getIndex());
                    borrarProfesor(p);
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
