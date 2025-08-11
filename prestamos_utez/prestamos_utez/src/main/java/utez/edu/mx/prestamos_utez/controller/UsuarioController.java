package utez.edu.mx.prestamos_utez.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.UsuarioImplDao;
import utez.edu.mx.prestamos_utez.model.Usuario;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;



import java.io.IOException;

public class UsuarioController {
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPass;

    @FXML
    public void initialize() {
        // Puedes agregar lógica de bienvenida aquí
        System.out.println("Ventana principal cargada");
    }

    @FXML
    private void onLogin(ActionEvent event){
        String correo = txtCorreo.getText().trim();
        String pass   = txtPass.getText().trim();

        UsuarioImplDao dao = new UsuarioImplDao();
        try {
            Usuario usuarioBD = dao.login(correo, pass);
            if (usuarioBD != null) {
                AdministradorSesion.setUsuarioActual(usuarioBD);
                System.out.println("Usuario Validado");

                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/utez/edu/mx/prestamos_utez/view/profesor_list.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("REBUP");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

                // cerrar login
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            } else {
                showError("Credenciales Incorrectas");
            }
        } catch (Exception e){
            e.printStackTrace();
            showError("Error al intentar iniciar sesión");
        }
    }

    private void showError(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    public void showAlertA(String title, String msg){
        //Alert alert=new Alert(Alert.AlertType.Error);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}

