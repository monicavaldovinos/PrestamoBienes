package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

public class CerrarSesionController {

    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private Stage owner;   // ventana principal
    private Stage dialog;  // este diálogo

    // la llama el listado al abrir el diálogo
    public void configure(Stage owner, Stage dialog) {
        this.owner = owner;
        this.dialog = dialog;
    }

    @FXML
    private void onCancelar() {
        dialog.close();
    }

    @FXML
    private void onConfirmar() {
        try {
            // 1) limpiar sesión
            AdministradorSesion.clear();

            // 2) cargar login y ponerlo en la ventana principal, maximizado
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/login.fxml"));
            Parent root = fx.load();

            Stage stage = (Stage) dialog.getOwner();
            stage.setScene(new Scene(root));
            stage.setTitle("REBUP - Login");
            stage.show();
            stage.setMaximized(true);
            stage.setFullScreen(true);
            dialog.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dialog.close();
        }
    }
}
