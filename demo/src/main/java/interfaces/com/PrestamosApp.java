package interfaces.com;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class PrestamosApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label lblTitulo = new Label("PRÉSTAMO");
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox topBar = new HBox(lblTitulo);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #00907A;");

        VBox sideMenu = new VBox(20);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setStyle("-fx-background-color: #093966;");
        sideMenu.setPrefWidth(150);

        Button btnPrestamos = new Button("Préstamos");
        Button btnDevoluciones = new Button("Devoluciones");
        btnPrestamos.setMaxWidth(Double.MAX_VALUE);
        btnDevoluciones.setMaxWidth(Double.MAX_VALUE);
        sideMenu.getChildren().addAll(btnPrestamos, btnDevoluciones);

        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10));
        TextField txtProfesor = new TextField();
        txtProfesor.setPromptText("Profesor");

        ComboBox<String> filtro = new ComboBox<>();
        filtro.getItems().addAll("Todos", "Disponibles", "No disponibles");
        filtro.setPromptText("Filtrar por");

        searchBar.getChildren().addAll(txtProfesor, filtro);
        searchBar.setAlignment(Pos.CENTER);

        String[] objetos = {"Switch", "Router", "Adaptador", "Proyector", "Cámara", "Laptop"};

        Map<String, String> rutasImagenes = Map.of(
                "Switch", "/Images/Switch.png",
                "Router", "/Images/Router.png",
                "Adaptador", "/Images/Adaptador.png",
                "Proyector", "/Images/Proyector.png",
                "Cámara", "/Images/Camara.png",
                "Laptop", "/Images/Laptop.png"
        );

        GridPane gridObjetos = new GridPane();
        gridObjetos.setHgap(20);
        gridObjetos.setVgap(20);
        gridObjetos.setPadding(new Insets(20));
        gridObjetos.setAlignment(Pos.CENTER);

        int col = 0, row = 0;
        for (String obj : objetos) {
            StackPane tarjeta = new StackPane();
            tarjeta.setPrefSize(150, 180);
            tarjeta.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10;");

            VBox contenido = new VBox(10);
            contenido.setAlignment(Pos.CENTER);

            ImageView imageView;
            try {
                imageView = new ImageView(new Image(getClass().getResourceAsStream(rutasImagenes.get(obj))));
                if (imageView.getImage().isError()) throw new Exception();
            } catch (Exception e) {
                imageView = new ImageView();
            }

            imageView.setFitWidth(60);
            imageView.setFitHeight(60);

            Label lblNombre = new Label(obj);
            lblNombre.setStyle("-fx-font-weight: bold;");

            contenido.getChildren().addAll(imageView, lblNombre);

            Button btnAdd = new Button("+");
            btnAdd.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white;");
            btnAdd.setOnAction(e -> System.out.println("Agregado: " + obj));
            StackPane.setAlignment(btnAdd, Pos.TOP_RIGHT);
            StackPane.setMargin(btnAdd, new Insets(5, 5, 0, 0));

            tarjeta.getChildren().addAll(contenido, btnAdd);
            gridObjetos.add(tarjeta, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        Button btnConfirmar = new Button("Confirmar préstamo");
        btnConfirmar.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white;");
        btnConfirmar.setOnAction(e -> mostrarConfirmacion());

        VBox contentArea = new VBox(10, topBar, searchBar, gridObjetos, btnConfirmar);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(10));
        contentArea.setPrefWidth(850);

        HBox root = new HBox(sideMenu, contentArea);
        Scene scene = new Scene(root, 1000, 600);

        primaryStage.setTitle("Préstamos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarConfirmacion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar préstamo");
        confirm.setHeaderText("¿Estás seguro de confirmar préstamo?");
        confirm.getButtonTypes().setAll(new ButtonType("¡Sí, confirmar!"), ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(type -> {
            if (type.getText().contains("Sí")) {
                Alert exito = new Alert(Alert.AlertType.INFORMATION, "Préstamo confirmado.");
                exito.show();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}