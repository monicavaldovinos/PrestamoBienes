package utez.com.mx.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class principalEncargado {

    public void mostrar() {
        // Menú lateral
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom right, #0054A0, #00907A);");
        sidebar.setPrefWidth(180);

        String[] opciones = {
                "Profesores", "Objetos", "Préstamos", "Devoluciones", "Historial", "Cerrar sesión"
        };
        for (String texto : opciones) {
            Button btn = new Button(texto);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.getStyleClass().add("sidebar-button");
            sidebar.getChildren().add(btn);

            if (texto.equals("Cerrar sesión")) {
                btn.setOnAction(e -> ((Stage) btn.getScene().getWindow()).close());
            }

            if (texto.equals("Préstamos")) {
                btn.setOnAction(e -> {
                    new PrestamosUI().mostrar(); // abrir pantalla de préstamos
                    ((Stage) btn.getScene().getWindow()).close(); // opcional: cerrar la pantalla actual
                });
            }

        }

        // Título y formulario
        Label titulo = new Label("REGISTRAR OBJETO");
        titulo.setId("section-title");

        VBox formulario = new VBox(10);
        formulario.setPadding(new Insets(20));
        formulario.setAlignment(Pos.TOP_LEFT);
        formulario.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        formulario.getChildren().addAll(
                new Label("Número de inventario:"),
                new TextField(),
                new Label("Número de serie:"),
                new TextField(),
                new Label("Nombre del objeto:"),
                new TextField(),
                new Label("Descripción (opcional):"),
                new TextField(),
                new Button("Agregar")
        );

        VBox centerPane = new VBox(15, titulo, formulario);
        centerPane.setPadding(new Insets(30));
        centerPane.setStyle("-fx-background-color: #f0f2f5;");

        // Organización general
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Sistema de Inventario");
        stage.setScene(scene);
        stage.show();
    }
}

