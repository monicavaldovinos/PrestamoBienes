package utez.com.mx.demo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloAplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Iniciar sesión");
        title.setId("title");

        TextField username = new TextField();
        username.setPromptText("Usuario");

        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");

        Button loginButton = new Button("Ingresar");
        Label message = new Label();

        loginButton.setOnAction(e -> {
            if ("admin".equals(username.getText()) && "1234".equals(password.getText())) {
                message.setText("Acceso concedido");
            } else {
                message.setText("Credenciales incorrectas");
            }

            if ("admin".equals(username.getText()) && "1234".equals(password.getText())) {
                new principalEncargado().mostrar(); // Lanza la pantalla nueva
                ((Stage) loginButton.getScene().getWindow()).close(); // Cierra login (opcional)
            }

        });

        VBox loginBox = new VBox(12, title, username, password, loginButton, message);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(25));
        loginBox.getStyleClass().add("login-box");
        loginBox.setPrefWidth(260);
        loginBox.setPrefHeight(200);
        loginBox.setMaxWidth(260);
        loginBox.setMaxHeight(200);

        StackPane root = new StackPane(loginBox);
        root.setPrefSize(400, 300);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0054A0, #00907A);");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
