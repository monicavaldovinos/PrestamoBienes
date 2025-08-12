package com.rebup;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rebup.dao.ObjetoDao;
import com.rebup.db.DBConnection;
import com.rebup.model.Objeto;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML private GridPane gridEquipos;
    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button btnConfirmar;
    @FXML private Label lblContador;
    @FXML private ImageView logo;

    private Map<Integer, Integer> seleccionados = new HashMap<>();
    private ObjetoDao objetoDao;

   @FXML
public void initialize() {
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        System.out.println("¡Conexion exitosa!");
    } catch (Exception e) {
        e.printStackTrace();
 
    }

    objetoDao = new ObjetoDao(conn);

    Image img = new Image(getClass().getResourceAsStream("/com/rebup/images/rebup.png"));
    logo.setImage(img);

    comboFiltro.getItems().addAll("Todos", "Disponible", "No disponible");
    comboFiltro.setValue("Todos");
    comboFiltro.setOnAction(e -> mostrarEquipos(comboFiltro.getValue()));

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

        List<Objeto> objetos;
        if (filtro.equals("Todos")) {
            objetos = objetoDao.listarTodos();
        } else if (filtro.equals("Disponible")) {
            objetos = objetoDao.listarDisponibles();
        } else {
            objetos = objetoDao.listarNoDisponibles();
        }

        int columna = 0, fila = 0;
        for (Objeto obj : objetos) {
            VBox contenedor = new VBox(5);
            contenedor.setAlignment(Pos.CENTER);
            contenedor.setPadding(new Insets(10));

            Image imagen;
            try {
                if (obj.getImagenUrl() != null && !obj.getImagenUrl().isEmpty()) {
                    imagen = new Image(obj.getImagenUrl());
                } else {
                    imagen = new Image(getClass().getResourceAsStream("/com/rebup/images/default.png"));
                }
                if (imagen.isError()) continue;
            } catch (Exception e) {
                continue;
            }

            ImageView imageView = new ImageView(imagen);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setCursor(Cursor.HAND);

            Label nombreLabel = new Label(obj.getNombre());
            nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Button btnAgregar = new Button("+");
            btnAgregar.setStyle("-fx-background-color: #00907A; -fx-text-fill: white;");
            Button btnQuitar = new Button("-");
            btnQuitar.setStyle("-fx-background-color: #E94B3C; -fx-text-fill: white;");
            btnQuitar.setDisable(true);

            Label cantidadLabel = new Label("0");
            cantidadLabel.setStyle("-fx-font-size: 14px;");

            HBox controles = new HBox(10, btnQuitar, cantidadLabel, btnAgregar);
            controles.setAlignment(Pos.CENTER);

            btnAgregar.setOnAction(e -> {
                int cant = seleccionados.getOrDefault(obj.getIdObjeto(), 0) + 1;
                seleccionados.put(obj.getIdObjeto(), cant);
                cantidadLabel.setText(String.valueOf(cant));
                btnQuitar.setDisable(false);
                btnConfirmar.setDisable(false);
                actualizarContador();
            });

            btnQuitar.setOnAction(e -> {
                int cant = seleccionados.getOrDefault(obj.getIdObjeto(), 0);
                if (cant > 1) {
                    cant--;
                    seleccionados.put(obj.getIdObjeto(), cant);
                    cantidadLabel.setText(String.valueOf(cant));
                } else {
                    seleccionados.remove(obj.getIdObjeto());
                    cantidadLabel.setText("0");
                    btnQuitar.setDisable(true);
                }
                if (seleccionados.isEmpty()) {
                    btnConfirmar.setDisable(true);
                }
                actualizarContador();
            });

            contenedor.getChildren().addAll(imageView, nombreLabel, controles);
            gridEquipos.add(contenedor, columna, fila);

            columna++;
            if (columna == 3) {
                columna = 0;
                fila++;
            }
        }
    }

    private void actualizarContador() {
        if (lblContador != null) {
            if (seleccionados.isEmpty()) {
                lblContador.setText("No has seleccionado objetos");
            } else {
                int total = seleccionados.values().stream().mapToInt(Integer::intValue).sum();
                lblContador.setText("Llevas seleccionado(s) " + total + " objeto(s)");
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

        VBox listaSeleccion = new VBox(5);
        listaSeleccion.setPadding(new Insets(10, 0, 20, 0));
        seleccionados.forEach((id, cant) -> {
            String texto = cant + " objeto(s) con ID: " + id;
            Label lbl = new Label(texto);
            lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            listaSeleccion.getChildren().add(lbl);
        });

        Button btnEliminar = new Button("Eliminar selección");
        btnEliminar.setPrefSize(150, 50);
        btnEliminar.setOnAction(e -> {
            seleccionados.clear();
            btnConfirmar.setDisable(true);
            alerta.close();
            mostrarEquipos(comboFiltro.getValue());
            escenaPrincipal.getRoot().setEffect(null);
        });

        Button btnSi = new Button("¡Sí, confirmar!");
        btnSi.setPrefSize(150, 50);
        btnSi.setOnAction(e -> {
            alerta.close();
            seleccionados.clear();
            btnConfirmar.setDisable(true);
            mostrarEquipos(comboFiltro.getValue());
            escenaPrincipal.getRoot().setEffect(null);
        });

        Button btnNo = new Button("Cancelar");
        btnNo.setPrefSize(150, 50);
        btnNo.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        HBox botones = new HBox(20, btnEliminar, btnNo, btnSi);
        botones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, mensaje, listaSeleccion, botones);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

        Scene scene = new Scene(layout, 600, 400);
        alerta.setScene(scene);
        alerta.centerOnScreen();
        alerta.setOnCloseRequest(e -> escenaPrincipal.getRoot().setEffect(null));
        alerta.showAndWait();
    }
}
