package utez.edu.mx.prestamos_utez.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.model.Profesor;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfesorFormController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<String> cbDivision;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDivisiones();
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String division = cbDivision.getValue();

        Profesor profesor = new Profesor();
        profesor.setNombre(nombre);
        profesor.setDivision(division);

        IProfesorDao dao = new ProfesorImplDao();
        boolean resultado = dao.create(profesor);

        if (resultado) {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("No se pudo guardar el profesor");
        }
    }

    private void cargarDivisiones() {
        cbDivision.getItems().addAll(
                "DATID",
                "DAMI",
                "DACEA",
                "DATEFI"
        );
    }

}

