package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

import java.net.URL;

public class MainLayoutController {

    @FXML private Label tituloPrincipal;
    @FXML private StackPane contenidoCentral;
    @FXML private VBox sidebar;

    // Botones del sidebar que controlamos por rol
    @FXML private Button btnPersonalizar;
    @FXML private Button btnUsuarios;
    @FXML private Button btnAreas;
    @FXML private Button btnDivisiones;
    @FXML private Button btnPendientes;

    @FXML
    private void initialize() {
        // Carga por defecto
        mostrarProfesores();

        var usuario = AdministradorSesion.getUsuarioActual();
        if (usuario == null) return;

        String rol = usuario.getNombreRol() != null ? usuario.getNombreRol() : "";

        if ("Encargado".equalsIgnoreCase(rol)) {
            if (btnPersonalizar != null) sidebar.getChildren().remove(btnPersonalizar);
            if (btnUsuarios != null)     sidebar.getChildren().remove(btnUsuarios);
            // Áreas y Divisiones permanecen ocultos
        }

        if ("Administrador".equalsIgnoreCase(rol)) {
            if (btnAreas != null) {
                btnAreas.setVisible(true);
                btnAreas.setManaged(true);
            }
            if (btnDivisiones != null) {
                btnDivisiones.setVisible(true);
                btnDivisiones.setManaged(true);
            }
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
        cargarVista("objeto_list.fxml");
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
    private void abrirUsuarios() {
        tituloPrincipal.setText("USUARIOS");
        cargarVista("usuario_list.fxml");
    }

    // NUEVO: handler que exige el FXML
    @FXML
    private void mostrarAreas() {
        tituloPrincipal.setText("ÁREAS");
        cargarVista("area_list.fxml");
    }
    @FXML
    private void mostrarDivisiones() {
        tituloPrincipal.setText("DIVISIONES");
        cargarVista("division_list.fxml");  // usa la misma utilidad
    }

    @FXML
    private void mostrarPendiente() {
        tituloPrincipal.setText("PENDIENTES");
        cargarVista("pendientes_list.fxml");  // usa la misma utilidad
    }


    @FXML
    private void mostrarCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/cerrar_sesion.fxml"
            ));
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 250);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cerrar sesión");
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.centerOnScreen();

            CerrarSesionController controller = loader.getController();
            controller.configure(
                    (Stage) tituloPrincipal.getScene().getWindow(),
                    dialogStage
            );

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cargarVista(String fxmlNombre) {
        try {
            URL recurso = getClass().getResource("/utez/edu/mx/prestamos_utez/view/" + fxmlNombre);
            if (recurso == null) {
                System.err.println("⚠️ No se encontró FXML: " + fxmlNombre);
                return;
            }
            Parent contenido = FXMLLoader.load(recurso);
            contenidoCentral.getChildren().setAll(contenido); // StackPane
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
