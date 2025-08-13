package com.rebup.ui.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rebup.dao.ObjetoDao;
import com.rebup.dao.ObjetoDaoImpl;
import com.rebup.model.Objeto;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML private GridPane gridEquipos;
    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button btnConfirmar;
    @FXML private ImageView logo;
    @FXML private Label lblContador;

    private Map<String, Integer> seleccionados = new HashMap<>();

    @FXML private Button btnPrestamos;
    @FXML private Button btnDevoluciones;
    @FXML private ComboBox<com.rebup.model.Profesor> cbProfesor;

    private Connection conn;
    private ObjetoDao objetoDao;

    @FXML
    public void initialize() {
        try {
            System.setProperty("oracle.net.tns_admin", "C:\\Users\\danie\\Desktop\\Wallet_LHM7CXPCQ0N4JJYB");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@lhm7cxpcq0n4jjyb_high", "ADMIN", "Ebarravaldo7#");
            objetoDao = new ObjetoDaoImpl(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        var daoProfesor = new com.rebup.dao.ProfesorDao();
        var dataProfesor = FXCollections.observableArrayList(daoProfesor.listar());
        var filtrado = new FilteredList<>(dataProfesor, p -> true);
        cbProfesor.setItems(filtrado);

        cbProfesor.getEditor().textProperty().addListener((obs, old, txt) -> {
            String q = txt == null ? "" : txt.toLowerCase();
            filtrado.setPredicate(p -> p.getNombre().toLowerCase().contains(q));
            if (!cbProfesor.isShowing()) cbProfesor.show();
        });

        cbProfesor.setOnAction(e -> {
            var sel = cbProfesor.getValue();
            if (sel != null) {
                System.out.println("Profesor seleccionado -> id=" + sel.getId() + ", nombre=" + sel.getNombre());
            }
        });

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
                mostrarResumenSeleccion();
            }
        });

        mostrarEquipos("Todos");
        actualizarContador();
    }

    private Map<String, Integer> maxCantidadesPorTipo = new HashMap<>();

   private void mostrarEquipos(String filtro) {
    gridEquipos.getChildren().clear();
    btnConfirmar.setDisable(seleccionados.isEmpty());
    actualizarContador();

    List<Objeto> objetosFiltrados;

    switch (filtro) {
        case "Disponible":
            objetosFiltrados = objetoDao.listarDisponibles();
            break;
        case "No disponible":
            objetosFiltrados = objetoDao.listarNoDisponibles();
            break;
        default:
            objetosFiltrados = objetoDao.listarTodos();
            break;
    }

    maxCantidadesPorTipo.clear();
    for (Objeto obj : objetosFiltrados) {
        String tipo = (obj.getTipo() == null || obj.getTipo().isBlank()) ? "desconocido" : obj.getTipo().toLowerCase();
        int cantidad = obj.getCantidad();
        maxCantidadesPorTipo.put(tipo, maxCantidadesPorTipo.getOrDefault(tipo, 0) + cantidad);
    }

    int columna = 0, fila = 0;
    Set<String> tiposAgregados = new HashSet<>();

    for (Objeto obj : objetosFiltrados) {
        String tipoOriginal = obj.getTipo();
        final String tipoFinal;
        if (tipoOriginal == null || tipoOriginal.isBlank()) {
            tipoFinal = "desconocido";
        } else {
            tipoFinal = tipoOriginal.toLowerCase();
        }

        if (tiposAgregados.contains(tipoFinal)) continue;
        tiposAgregados.add(tipoFinal);

        VBox contenedor = new VBox(5);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(10));

        String rutaImagen = "/com/rebup/images/" + tipoFinal + ".png";
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

        StackPane imageWithOverlay = new StackPane();
        imageWithOverlay.setPrefSize(120, 120);

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

        int cantidadSeleccionada = seleccionados.getOrDefault(tipoFinal, 0);
        minusCircle.setVisible(cantidadSeleccionada > 0);

        imageWithOverlay.getChildren().addAll(imageView, plusCircle, minusCircle);

        StackPane imageBorderPane = new StackPane(imageWithOverlay);
        imageBorderPane.setPrefSize(130, 130);
        imageBorderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        String textoBoton = (obj.getNombre() != null && !obj.getNombre().isEmpty())
                ? obj.getNombre()
                : tipoFinal.substring(0, 1).toUpperCase() + tipoFinal.substring(1);

        Button botonObjeto = new Button(textoBoton);
        botonObjeto.setPrefSize(110, 25);
        botonObjeto.setStyle("-fx-background-color: #00907A; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8;");

        imageView.setOnMouseClicked(e -> {
            agregarObjeto(tipoFinal, imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        botonObjeto.setOnAction(e -> {
            agregarObjeto(tipoFinal, imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        plusCircle.setOnMouseClicked(e -> {
            agregarObjeto(tipoFinal, imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        minusCircle.setOnMouseClicked(e -> {
            disminuirObjeto(tipoFinal, imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });

        contenedor.getChildren().addAll(imageBorderPane, botonObjeto);

        VBox contenedorCompleto = new VBox(5);
        contenedorCompleto.setAlignment(Pos.CENTER);
        contenedorCompleto.getChildren().add(contenedor);

        int maxCantidad = maxCantidadesPorTipo.getOrDefault(tipoFinal, 0);

        if (cantidadSeleccionada >= maxCantidad) {
            StackPane alertaWrapper = new StackPane();
            alertaWrapper.getChildren().add(contenedorCompleto);

            HBox alertaNoMasBox = new HBox();
            alertaNoMasBox.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-radius: 6; -fx-background-radius: 6;");
            alertaNoMasBox.setPadding(new Insets(3, 8, 3, 8));
            alertaNoMasBox.setMaxWidth(170);
            alertaNoMasBox.setMinHeight(18);
            alertaNoMasBox.setMaxHeight(18);
            alertaNoMasBox.setAlignment(Pos.CENTER_LEFT);
            alertaNoMasBox.setSpacing(5);

            Label alertaTexto = new Label("¡NO HAY MAS OBJETOS!");
            alertaTexto.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10px;");
            alertaTexto.setWrapText(false);

            Button btnCerrar = new Button("X");
            btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 0;");
            btnCerrar.setOnAction(ev -> alertaWrapper.getChildren().remove(alertaNoMasBox));

            alertaNoMasBox.getChildren().addAll(alertaTexto, btnCerrar);

            alertaWrapper.getChildren().add(alertaNoMasBox);
            StackPane.setAlignment(alertaNoMasBox, Pos.TOP_RIGHT);
            StackPane.setMargin(alertaNoMasBox, new Insets(-10, -10, 0, 0));

            gridEquipos.add(alertaWrapper, columna, fila);
        } else {
            gridEquipos.add(contenedorCompleto, columna, fila);
        }

        columna++;
        if (columna == 3) {
            columna = 0;
            fila++;
        }
    }
}


    private void agregarObjeto(String tipo, StackPane borderPane, StackPane minusCircle) {
        int maxCantidad = maxCantidadesPorTipo.getOrDefault(tipo, Integer.MAX_VALUE);
        int actual = seleccionados.getOrDefault(tipo, 0);
        if (actual < maxCantidad) {
            seleccionados.put(tipo, actual + 1);
            borderPane.setStyle("-fx-border-color: #00907A; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
            minusCircle.setVisible(true);
            actualizarContador();
            btnConfirmar.setDisable(false);
        }
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

    private void mostrarResumenSeleccion() {
        Scene escenaPrincipal = btnConfirmar.getScene();
        GaussianBlur blur = new GaussianBlur(15);
        escenaPrincipal.getRoot().setEffect(blur);

        Stage resumenStage = new Stage();
        resumenStage.initModality(Modality.APPLICATION_MODAL);
        resumenStage.setTitle("Confirmación de préstamo");

        Label titulo = new Label("¿ESTÁS SEGURO DE CONFIRMAR EL PRÉSTAMO?");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox listaSeleccion = new VBox(5);
        listaSeleccion.setPadding(new Insets(10));
        for (Map.Entry<String, Integer> entry : seleccionados.entrySet()) {
            String texto = entry.getValue() + " × " + entry.getKey() + (entry.getValue() > 1 ? "s" : "");
            Label item = new Label(texto);
            item.setStyle("-fx-font-size: 16px;");
            listaSeleccion.getChildren().add(item);
        }

        Button btnEliminar = new Button("Eliminar selección");
        btnEliminar.setPrefSize(296, 73);
        btnEliminar.setStyle("-fx-background-color: #C2C2C2; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 18px;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefSize(296, 73);
        btnCancelar.setStyle("-fx-background-color: #C2C2C2; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 18px;");

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setPrefSize(296, 73);
        btnAceptar.setStyle("-fx-background-color: #4A6FDB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        btnEliminar.setOnAction(e -> {
            seleccionados.clear();
            actualizarContador();
            btnConfirmar.setDisable(true);
            resumenStage.close();
            Scene escenaPrincipal2 = btnConfirmar.getScene();
            if (escenaPrincipal2 != null) {
                escenaPrincipal2.getRoot().setEffect(null);
            }
            mostrarEquipos(comboFiltro.getValue());
        });

        btnCancelar.setOnAction(e -> {
            resumenStage.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        btnAceptar.setOnAction(e -> {
            try {
                guardarSeleccionados();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            resumenStage.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        HBox botones = new HBox(10, btnEliminar, btnCancelar, btnAceptar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10));

        VBox layout = new VBox(20, titulo, listaSeleccion, botones);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15;");

        Scene scene = new Scene(layout, 700, 420);
        resumenStage.setScene(scene);
        resumenStage.centerOnScreen();
        resumenStage.showAndWait();
    }

    private void guardarSeleccionados() throws SQLException {
        for (Map.Entry<String, Integer> entry : seleccionados.entrySet()) {
            String tipo = entry.getKey();
            int cantidad = entry.getValue();

            Objeto obj = new Objeto(
                0,
                tipo,
                tipo,
                "",
                "Disponible",
                cantidad,
                "",
                "",
                ""
            );

            objetoDao.insertarObjeto(obj);
        }

        mostrarConfirmacion();
        seleccionados.clear();
        actualizarContador();
        btnConfirmar.setDisable(true);
        mostrarEquipos(comboFiltro.getValue());
    }

    private void mostrarConfirmacion() {
        Scene escenaPrincipal = btnConfirmar.getScene();
        GaussianBlur blur = new GaussianBlur(15);
        escenaPrincipal.getRoot().setEffect(blur);

        Stage alerta = new Stage();
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.setTitle("Confirmación");

        Image checkImg = new Image(getClass().getResourceAsStream("/com/rebup/images/check.png"));
        ImageView ivCheck = new ImageView(checkImg);
        ivCheck.setFitWidth(60);
        ivCheck.setFitHeight(60);

        Label mensaje = new Label("¡Préstamo confirmado!");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black;");

        VBox vboxMensaje = new VBox(10, ivCheck, mensaje);
        vboxMensaje.setAlignment(Pos.CENTER);

        Button btnContinuar = new Button("Continuar");
        btnContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-border-color: black; -fx-padding: 10 40;");
        btnContinuar.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        VBox layout = new VBox(20, vboxMensaje, btnContinuar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");
        Scene scene = new Scene(layout, 400, 250);
alerta.setScene(scene);
alerta.centerOnScreen();
alerta.showAndWait();
}
}


       
