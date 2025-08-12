package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

import java.io.IOException;
import java.net.URL;

public class MainLayoutController {

    @FXML private Label tituloPrincipal;
    @FXML private StackPane contenidoCentral;  // <--- coincide con el FXML
    @FXML private Button btnPersonalizar;
    @FXML private Button btnUsuarios;
    @FXML private VBox sidebar;

    @FXML
    private void initialize() {
        mostrarProfesores();
        var usuario = AdministradorSesion.getUsuarioActual();
        if (usuario != null && "Encargado".equalsIgnoreCase(usuario.getNombreRol())) {
            if (btnPersonalizar != null && sidebar != null) sidebar.getChildren().remove(btnPersonalizar);
            if (btnUsuarios != null && sidebar != null) sidebar.getChildren().remove(btnUsuarios);
        }
    }

    @FXML
    private void mostrarProfesores() {
        tituloPrincipal.setText("PROFESORES");
        cargarVista("profesor_list.fxml");
    }

    @FXML
    private void mostrarObjetos() {
        tituloPrincipal.setText("OBJETOS");
        cargarVista("Objeto_list.fxml");
    }

    @FXML
    private void mostrarHistorial() {
        tituloPrincipal.setText("HISTORIAL");
        cargarVista("historial_list.fxml");
    }

    @FXML
    private void mostrarPersonalizar() {
        tituloPrincipal.setText("PERSONALIZAR");
        cargarVista("personalizar.fxml");
    }

    @FXML
    private void mostrarCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utez/edu/mx/prestamos_utez/view/cerrar_sesion.fxml"));
            Parent root = loader.load();
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Cerrar sesión");
            dialogStage.setScene(new javafx.scene.Scene(root, 400, 250));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.centerOnScreen();
            // Configurar el controlador con las ventanas
            utez.edu.mx.prestamos_utez.controller.CerrarSesionController controller = loader.getController();
            controller.configure(tituloPrincipal.getScene().getWindow() instanceof javafx.stage.Stage ? (javafx.stage.Stage) tituloPrincipal.getScene().getWindow() : null, dialogStage);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirUsuarios() {
        tituloPrincipal.setText("USUARIOS");
        cargarVista("usuario_list.fxml");
    }

    private void cargarVista(String fxmlNombre) {
        try {
            URL recurso = getClass().getResource("/utez/edu/mx/prestamos_utez/view/" + fxmlNombre);
            if (recurso == null) {
                System.err.println("⚠️ No se encontró: " + fxmlNombre);
                return;
            }
            Parent contenido = FXMLLoader.load(recurso);
            contenidoCentral.getChildren().setAll(contenido); // <--- StackPane
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
