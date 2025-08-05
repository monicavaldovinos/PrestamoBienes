package utez.edu.mx.prestamos_utez.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.model.Profesor;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfesorListController implements Initializable {

    @FXML
    private TableView<Profesor> tablaProfesores;
    @FXML
    private TableColumn<Profesor, String> colNombre;
    @FXML
    private TableColumn<Profesor, String> colDivision;
    @FXML
    private Button btnAgregarProfesor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        cargarDatos();
        btnAgregarProfesor.setOnAction(event -> abrirFormularioModal());
    }

    private void cargarDatos() {
        IProfesorDao dao = new ProfesorImplDao();
        List<Profesor> lista = dao.obtenerTodos();
        tablaProfesores.getItems().setAll(lista);
    }

    private void abrirFormularioModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utez/edu/mx/prestamos_utez/view/profesor_form.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("Agregar Profesor");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}