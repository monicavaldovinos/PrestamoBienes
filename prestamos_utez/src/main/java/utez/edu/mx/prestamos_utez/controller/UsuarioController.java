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
        String correo= txtCorreo.getText().trim();
        String pass = txtPass.getText().trim();
        UsuarioImplDao dao= new UsuarioImplDao();
        try{
            if(dao.login(correo,pass)){
                System.out.println("Usuario Validado");
                //showAlertA("BIENVENIDO", "Bienvenido");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/utez/edu/mx/prestamos_utez/view/profesor.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Bienvenido");

                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();


                // Cerrar la ventana actual
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();


            }else{
                showAlert("Error", "Credenciales Incorrectas");
                System.out.println("Credenciales Incorrectas");
            }

        }catch (Exception e){
            //System.out.println("Error");
            showAlert("Error", "Error al intentar logear");
        }
    }


    public void showAlert(String title, String msg){
        //Alert alert=new Alert(Alert.AlertType.Error);
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
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

