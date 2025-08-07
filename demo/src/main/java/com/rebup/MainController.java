package com.rebup;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private GridPane gridEquipos;

    @FXML
    private ComboBox<String> comboFiltro;

    @FXML
    private Button btnConfirmar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ImageView logo;

    private boolean haySeleccion = false;

    @FXML
    public void initialize() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/rebup/images/rebup.png"));
            logo.setImage(img);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen rebup.png");
        }

        comboFiltro.getItems().addAll("Todos", "Disponible", "No disponible");
        comboFiltro.setValue("Todos");
        comboFiltro.setOnAction(e -> mostrarEquipos(comboFiltro.getValue()));
        mostrarEquipos("Todos");

        haySeleccion = false;
        btnConfirmar.setDisable(true);

        btnConfirmar.setOnAction(e -> {
            if (haySeleccion) {
                mostrarAlerta();
            }
        });
    }

    private void mostrarEquipos(String filtro) {
        gridEquipos.getChildren().clear();

        List<String> tipos = List.of("switch", "router", "adaptador", "proyector", "camara", "laptop");

        int columna = 0, fila = 0;

        for (String tipo : tipos) {
            if (!filtro.equals("Todos")) {
                if (filtro.equals("Disponible") && (tipo.equalsIgnoreCase("laptop") || tipo.equalsIgnoreCase("proyector"))) continue;
                if (filtro.equals("No disponible") && (tipo.equalsIgnoreCase("switch") || tipo.equalsIgnoreCase("laptop") || tipo.equalsIgnoreCase("proyector"))) continue;
            }

            VBox contenedor = new VBox(3);
            contenedor.setAlignment(Pos.CENTER);

            String rutaImagen = "/com/rebup/images/" + tipo.toLowerCase() + ".png";
            Image imagen;
            try {
                imagen = new Image(getClass().getResourceAsStream(rutaImagen));
                if (imagen.isError()) continue;
            } catch (Exception e) {
                continue;
            }

            ImageView imageView = new ImageView(imagen);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setCursor(Cursor.HAND);
            imageView.setOnMouseClicked(e -> mostrarConfirmacion(tipo));

            StackPane stack = new StackPane(imageView);
            stack.setAlignment(Pos.TOP_RIGHT);

            String textoBoton = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
            Button botonObjeto = new Button(textoBoton);
            botonObjeto.setPrefSize(110, 25);
            botonObjeto.setStyle(
                "-fx-background-color: #00907A; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8;"
            );
            botonObjeto.setOnAction(e -> mostrarConfirmacion(tipo));

            contenedor.getChildren().addAll(stack, botonObjeto);
            gridEquipos.add(contenedor, columna, fila);

            columna++;
            if (columna == 3) {
                columna = 0;
                fila++;
            }
        }
    }

    private void mostrarConfirmacion(String tipo) {
        Stage alerta = new Stage();
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Confirmación");

        Label mensaje = new Label("¿ESTÁS SEGURO DE CONFIRMAR PRÉSTAMO?");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black; -fx-font-weight: bold;");

        Button btnSi = new Button("¡Sí, confirmar!");
        btnSi.setPrefSize(296, 73);
        btnSi.setStyle(
            "-fx-background-color: #4A6FDB; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-font-weight: bold;"
        );
        btnSi.setOnAction(e -> {
            haySeleccion = true;
            btnConfirmar.setDisable(false);
            alerta.close();
        });

        Button btnNo = new Button("Cancelar");
        btnNo.setPrefSize(296, 73);
        btnNo.setStyle(
            "-fx-background-color: #DC3030; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-font-weight: bold;"
        );
        btnNo.setOnAction(e -> alerta.close());

        HBox botones = new HBox(40, btnSi, btnNo);
        botones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(60, mensaje, botones);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

        Scene scene = new Scene(layout, 737, 521);
        alerta.setScene(scene);
        alerta.showAndWait();
    }

    private void mostrarAlerta() {
        Stage alerta = new Stage();
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Confirmación");

        Label mensaje = new Label("¡Préstamo confirmado!");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black;");

        Button btnContinuar = new Button("Continuar");
        btnContinuar.setStyle(
            "-fx-background-color: white; -fx-text-fill: black; " +
            "-fx-font-size: 18px; -fx-border-color: black; " +
            "-fx-padding: 10 40;"
        );
        btnContinuar.setOnAction(e -> alerta.close());

        VBox layout = new VBox(100, mensaje, btnContinuar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 737, 521);
        alerta.setScene(scene);
        alerta.showAndWait();
    }
}
