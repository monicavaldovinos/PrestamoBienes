package utez.edu.mx.prestamos_utez;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.config.DBConnection;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/utez/edu/mx/prestamos_utez/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();


        // Centrar la ventana en la pantalla
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        stage.setX((pantalla.getWidth() - stage.getWidth()) / 2);
        stage.setY((pantalla.getHeight() - stage.getHeight()) / 2);
    }

    public static void main(String[] args) {

        launch();
    }
}