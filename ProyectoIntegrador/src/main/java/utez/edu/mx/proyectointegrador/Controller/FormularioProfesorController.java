package utez.edu.mx.proyectointegrador.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioProfesorController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDivision;
    @FXML private TextField txtMateria;

    @FXML
    private void agregarProfesor() {
        // Aquí haces la lógica para guardar el profesor
        System.out.println("Nombre: " + txtNombre.getText());
        cerrarVentana();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}

