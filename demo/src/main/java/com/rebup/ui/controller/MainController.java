package com.rebup.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML private GridPane gridEquipos;
    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button btnConfirmar;
    @FXML private TextField txtBuscar;
    @FXML private ImageView logo;
    @FXML private Label lblContador;

    private Map<String, Integer> seleccionados = new HashMap<>();

    @FXML private Button btnPrestamos;
    @FXML private Button btnDevoluciones;

    @FXML
    public void initialize() {
        Image img = new Image(getClass().getResourceAsStream("/com/rebup/images/rebup.png"));
        logo.setImage(img);

        Image iconPrestamos = new Image(getClass().getResourceAsStream("/com/rebup/images/prestamos.png"));
        ImageView ivPrestamos = new ImageView(iconPrestamos);
        ivPrestamos.setFitWidth(20);
        ivPrestamos.setFitHeight(20);
        btnPrestamos.setGraphic(ivPrestamos);

        Image iconDevoluciones = new Image(getClass().getResourceAsStream("/com/rebup/images/devoluciones.png"));
        ImageView ivDevoluciones = new ImageView(iconDevoluciones);
        ivDevoluciones.setFitWidth(20);
        ivDevoluciones.setFitHeight(20);
        btnDevoluciones.setGraphic(ivDevoluciones);

        comboFiltro.getItems().addAll("Todos", "Disponible", "No disponible");
        comboFiltro.setValue("Todos");
        comboFiltro.setOnAction(e -> mostrarEquipos(comboFiltro.getValue()));
        comboFiltro.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 5; -fx-border-radius: 5;");

        btnConfirmar.setDisable(true);

        btnConfirmar.setOnAction(e -> {
            if (!seleccionados.isEmpty()) {
                mostrarConfirmacion();
            }
        });

        mostrarEquipos("Todos");
        actualizarContador();
    }

    private void mostrarEquipos(String filtro) {
        gridEquipos.getChildren().clear();
        seleccionados.clear();
        btnConfirmar.setDisable(true);
        actualizarContador();

        List<String> tipos = List.of("switch", "router", "adaptador", "proyector", "camara", "laptop");

        int columna = 0, fila = 0;

        for (String tipo : tipos) {
            if (!filtro.equals("Todos")) {
                if (filtro.equals("Disponible") && (tipo.equalsIgnoreCase("laptop") || tipo.equalsIgnoreCase("proyector")))
                    continue;
                if (filtro.equals("No disponible") && (tipo.equalsIgnoreCase("switch") || tipo.equalsIgnoreCase("laptop") || tipo.equalsIgnoreCase("proyector")))
                    continue;
            }

            VBox contenedor = new VBox(5);
            contenedor.setAlignment(Pos.CENTER);
            contenedor.setPadding(new Insets(10));

            String rutaImagen = "/com/rebup/images/" + tipo.toLowerCase() + ".png";
            Image imagen;
            try {
                imagen = new Image(getClass().getResourceAsStream(rutaImagen));
                if (imagen.isError())
                    continue;
            } catch (Exception e) {
                continue;
            }

            ImageView imageView = new ImageView(imagen);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setCursor(Cursor.HAND);

            StackPane imageWithPlus = new StackPane();
            imageWithPlus.setPrefSize(120, 120);

            Label plusLabel = new Label("+");
            plusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

            StackPane plusCircle = new StackPane(plusLabel);
            plusCircle.setStyle("-fx-background-color: #00907A; -fx-background-radius: 50%;");
            plusCircle.setPrefSize(24, 24);
            plusCircle.setMaxSize(24, 24);
            StackPane.setAlignment(plusCircle, Pos.TOP_RIGHT);
            StackPane.setMargin(plusCircle, new Insets(3, 3, 0, 0)); 

            Label minusLabel = new Label("-");
            minusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

            StackPane minusCircle = new StackPane(minusLabel);
            minusCircle.setStyle("-fx-background-color: #E94B3C; -fx-background-radius: 50%;");
            minusCircle.setPrefSize(24, 24);
            minusCircle.setMaxSize(24, 24);
            StackPane.setAlignment(minusCircle, Pos.TOP_LEFT);
            StackPane.setMargin(minusCircle, new Insets(3, 0, 0, 3));
            minusCircle.setVisible(false);

            imageWithPlus.getChildren().addAll(imageView, plusCircle, minusCircle);

            StackPane imageBorderPane = new StackPane(imageWithPlus);
            imageBorderPane.setPrefSize(130, 130);
            imageBorderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

            String textoBoton = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
            Button botonObjeto = new Button(textoBoton);
            botonObjeto.setPrefSize(110, 25);
            botonObjeto.setStyle("-fx-background-color: #00907A; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8;");

            imageView.setOnMouseClicked(e -> {
                agregarObjeto(tipo, imageBorderPane, minusCircle);
            });

            botonObjeto.setOnAction(e -> {
                agregarObjeto(tipo, imageBorderPane, minusCircle);
            });

            plusCircle.setOnMouseClicked(e -> {
                agregarObjeto(tipo, imageBorderPane, minusCircle);
            });

            minusCircle.setOnMouseClicked(e -> {
                disminuirObjeto(tipo, imageBorderPane, minusCircle);
            });

            contenedor.getChildren().addAll(imageBorderPane, botonObjeto);
            gridEquipos.add(contenedor, columna, fila);

            columna++;
            if (columna == 3) {
                columna = 0;
                fila++;
            }
        }
    }

    private void toggleSeleccion(String tipo, StackPane borderPane) {
        if (seleccionados.containsKey(tipo) && seleccionados.get(tipo) > 0) {
            seleccionados.remove(tipo);
            borderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
        } else {
            seleccionados.put(tipo, 1);
            borderPane.setStyle("-fx-border-color: #00907A; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        }
        actualizarContador();
        btnConfirmar.setDisable(seleccionados.isEmpty());
    }

    private void agregarObjeto(String tipo, StackPane borderPane, StackPane minusCircle) {
        seleccionados.put(tipo, seleccionados.getOrDefault(tipo, 0) + 1);
        borderPane.setStyle("-fx-border-color: #00907A; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        minusCircle.setVisible(true);
        actualizarContador();
        btnConfirmar.setDisable(false);
    }

    private void disminuirObjeto(String tipo, StackPane borderPane, StackPane minusCircle) {
        if (seleccionados.containsKey(tipo)) {
            int count = seleccionados.get(tipo);
            if (count > 1) {
                seleccionados.put(tipo, count - 1);
            } else {
                seleccionados.remove(tipo);
                borderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                minusCircle.setVisible(false);
            }
            actualizarContador();
            btnConfirmar.setDisable(seleccionados.isEmpty());
        }
    }

    private void actualizarContador() {
        if (lblContador != null) {
            if (seleccionados.isEmpty()) {
                lblContador.setText("No has seleccionado objetos");
            } else {
                StringBuilder sb = new StringBuilder();
                int total = 0;
                for (Map.Entry<String, Integer> entry : seleccionados.entrySet()) {
                    sb.append("Llevas ").append(entry.getValue()).append(" ").append(entry.getKey());
                    if (entry.getValue() > 1) sb.append("s");
                    sb.append("\n");
                    total += entry.getValue();
                }
                sb.append("Llevas en total ").append(total).append(" objeto");
                if (total != 1) sb.append("s");
                lblContador.setText(sb.toString());
            }
        }
    }

    private void mostrarConfirmacion() {
        Scene escenaPrincipal = btnConfirmar.getScene();
        GaussianBlur blur = new GaussianBlur(15);
        escenaPrincipal.getRoot().setEffect(blur);

        Stage alerta = new Stage();
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Confirmación");

        Label mensaje = new Label("¿ESTÁS SEGURO DE CONFIRMAR PRÉSTAMO?");
        mensaje.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");
        mensaje.setWrapText(true);

        Label seleccionTexto = new Label("Has seleccionado:");
        seleccionTexto.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox listaSeleccion = new VBox(5);
        listaSeleccion.setPadding(new Insets(10, 0, 20, 0));

        if (seleccionados.isEmpty()) {
            listaSeleccion.getChildren().add(new Label("No has seleccionado objetos."));
        } else {
            seleccionados.forEach((tipo, cantidad) -> {
                String texto = cantidad + " " + tipo;
                if (cantidad > 1) texto += "s";
                Label lbl = new Label(texto);
                lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                listaSeleccion.getChildren().add(lbl);
            });
        }

        Button btnEliminar = new Button("Eliminar selección");
        btnEliminar.setPrefSize(150, 50);
        btnEliminar.setStyle("-fx-background-color: #D3D3D3; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnEliminar.setOnAction(e -> {
            seleccionados.clear();
            btnConfirmar.setDisable(true);
            alerta.close();
            mostrarEquipos(comboFiltro.getValue());
            escenaPrincipal.getRoot().setEffect(null);
        });

        Button btnSi = new Button("¡Sí, confirmar!");
        btnSi.setPrefSize(150, 50);
        btnSi.setStyle("-fx-background-color: #4A6FDB; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnSi.setOnAction(e -> {
            alerta.close();
            mostrarAlerta();
            seleccionados.clear();
            btnConfirmar.setDisable(true);
            mostrarEquipos(comboFiltro.getValue());
            escenaPrincipal.getRoot().setEffect(null);
        });

        Button btnNo = new Button("Cancelar");
        btnNo.setPrefSize(150, 50);
        btnNo.setStyle("-fx-background-color: #D3D3D3; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnNo.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        HBox botones = new HBox(20, btnEliminar, btnNo, btnSi);
        botones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, mensaje, seleccionTexto, listaSeleccion, botones);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

        Scene scene = new Scene(layout, 600, 400);
        alerta.setScene(scene);
        alerta.centerOnScreen();
        alerta.setOnCloseRequest(e -> escenaPrincipal.getRoot().setEffect(null));
        alerta.showAndWait();
    }

    private void mostrarAlerta() {
        Scene escenaPrincipal = btnConfirmar.getScene();
        GaussianBlur blur = new GaussianBlur(15);
        escenaPrincipal.getRoot().setEffect(blur);

        Stage alerta = new Stage();
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Confirmación");

        ImageView checkIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/rebup/images/check.png")));
        checkIcon.setFitWidth(100);
        checkIcon.setFitHeight(100);

        Label mensaje = new Label("¡Préstamo confirmado!");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black;");

        Button btnContinuar = new Button("Continuar");
        btnContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-border-color: black; -fx-padding: 10 40;");
        btnContinuar.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        VBox layout = new VBox(40, checkIcon, mensaje, btnContinuar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

        Scene scene = new Scene(layout, 620, 500);
        alerta.show();
        alerta.setScene(scene);
        alerta.centerOnScreen();
        alerta.showAndWait();
    }
}
