package com.rebup.ui.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML private GridPane gridEquipos;
    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button btnConfirmar;
    @FXML private ImageView logo;
   

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

 

  private Map<Integer, Integer> maxCantidadesPorObjeto = new HashMap<>();

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

    maxCantidadesPorObjeto.clear();
    for (Objeto obj : objetosFiltrados) {
        maxCantidadesPorObjeto.put(obj.getIdObjeto(), obj.getCantidad());
    }

    int columna = 0, fila = 0;

    for (Objeto obj : objetosFiltrados) {
        VBox contenedor = new VBox(5);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(10));

        String imagenUrl = obj.getImagenUrl();
        Image imagen;
        try {
            if (imagenUrl != null && !imagenUrl.isBlank()) {
                imagen = new Image(imagenUrl, false);
            } else {
                imagen = new Image(getClass().getResourceAsStream("/com/rebup/images/default.png"));
            }
        } catch (Exception e) {
            imagen = new Image(getClass().getResourceAsStream("/com/rebup/images/default.png"));
        }

        ImageView imageView = new ImageView(imagen);
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setCursor(Cursor.HAND);

        StackPane imageWithOverlay = new StackPane(imageView);
        imageWithOverlay.setPrefSize(120, 120);       

        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        StackPane plusCircle = new StackPane(plusLabel);
        plusCircle.setStyle("-fx-background-color: #00907A; -fx-background-radius: 50%;");
        plusCircle.setPrefSize(24, 24);
        plusCircle.setMaxSize(24, 24);
        plusCircle.setMinSize(24, 24);
        StackPane.setAlignment(plusCircle, Pos.TOP_RIGHT);
        StackPane.setMargin(plusCircle, new Insets(3, 3, 0, 0));

        Label minusLabel = new Label("-");
        minusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        StackPane minusCircle = new StackPane(minusLabel);
        minusCircle.setStyle("-fx-background-color: #E94B3C; -fx-background-radius: 50%;");
        minusCircle.setPrefSize(24, 24);
        minusCircle.setMaxSize(24, 24);
        minusCircle.setMinSize(24, 24);
        StackPane.setAlignment(minusCircle, Pos.TOP_LEFT);  
        StackPane.setMargin(minusCircle, new Insets(3, 0, 0, 3));


        int cantidadSeleccionada = seleccionados.getOrDefault(String.valueOf(obj.getIdObjeto()), 0);
        minusCircle.setVisible(cantidadSeleccionada > 0);

        imageWithOverlay.getChildren().addAll(plusCircle, minusCircle);

        StackPane imageBorderPane = new StackPane(imageWithOverlay);
        imageBorderPane.setPrefSize(130, 130);         
        imageBorderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        Button botonObjeto = new Button(obj.getNombre());
        botonObjeto.setPrefSize(110, 25);
        botonObjeto.setStyle("-fx-background-color: #00907A; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8;");

        plusCircle.setOnMouseClicked(e -> {
            agregarObjeto(obj.getIdObjeto(), imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        imageView.setOnMouseClicked(e -> {
            agregarObjeto(obj.getIdObjeto(), imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        botonObjeto.setOnAction(e -> {
            agregarObjeto(obj.getIdObjeto(), imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });
        minusCircle.setOnMouseClicked(e -> {
            disminuirObjeto(obj.getIdObjeto(), imageBorderPane, minusCircle);
            mostrarEquipos(filtro);
        });

        contenedor.getChildren().addAll(imageBorderPane, botonObjeto);

        if (cantidadSeleccionada >= maxCantidadesPorObjeto.getOrDefault(obj.getIdObjeto(), 0)) {
            Label alertaTexto = new Label("¡NO HAY MÁS OBJETOS!");
            alertaTexto.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10px;");
            contenedor.getChildren().add(alertaTexto);
        }

        gridEquipos.add(contenedor, columna, fila);

        columna++;
        if (columna == 3) {
            columna = 0;
            fila++;
        }
    }
}


    private void agregarObjeto(int idObjeto, StackPane borderPane, StackPane minusCircle) {
    int maxCantidad = maxCantidadesPorObjeto.getOrDefault(idObjeto, Integer.MAX_VALUE);
    int actual = seleccionados.getOrDefault(String.valueOf(idObjeto), 0);
    if (actual < maxCantidad) {
        seleccionados.put(String.valueOf(idObjeto), actual + 1);
        borderPane.setStyle("-fx-border-color: #00907A; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        minusCircle.setVisible(true);
        actualizarContador();
        btnConfirmar.setDisable(false);
    }
}

private void disminuirObjeto(int idObjeto, StackPane borderPane, StackPane minusCircle) {
    String key = String.valueOf(idObjeto);
    if (seleccionados.containsKey(key)) {
        int count = seleccionados.get(key);
        if (count > 1) {
            seleccionados.put(key, count - 1);
        } else {
            seleccionados.remove(key);
            borderPane.setStyle("-fx-border-color: #D2D2D2; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
            minusCircle.setVisible(false);
        }
        actualizarContador();
        btnConfirmar.setDisable(seleccionados.isEmpty());
    }
}

@FXML
private VBox contadoresBox;
@FXML
private Label lblContador;

private void actualizarContador() {
    contadoresBox.getChildren().clear();
    int totalObjetos = 0;

    for (Map.Entry<String, Integer> entry : seleccionados.entrySet()) {
        int idObjeto = Integer.parseInt(entry.getKey());
        Objeto obj = objetoDao.obtenerPorId(idObjeto);
        String texto;
        if (obj != null) {
            texto = "Llevas " + entry.getValue() + " " + obj.getNombre();
            if (entry.getValue() > 1) texto += "s";
        } else {
            texto = "Llevas " + entry.getValue() + " objeto desconocido";
        }
        totalObjetos += entry.getValue();
        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        contadoresBox.getChildren().add(label);
    }

    lblContador.setText("Llevas en total " + totalObjetos );
    StackPane.setMargin(contadoresBox, new Insets(10, 0, 0, 0));
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

    Label subtitulo = new Label("Has seleccionado:");
    subtitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

    VBox listaSeleccion = new VBox(5);
    listaSeleccion.setPadding(new Insets(10));
    for (Map.Entry<String, Integer> entry : seleccionados.entrySet()) {
        int idObjeto = Integer.parseInt(entry.getKey());
        Objeto obj = objetoDao.obtenerPorId(idObjeto);
        String nombre = (obj != null) ? obj.getNombre() : "objeto desconocido";
        String texto = entry.getValue() + " " + nombre;
        if (entry.getValue() > 1) texto += "s";
        Label item = new Label(texto);
        item.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        listaSeleccion.getChildren().add(item);
    }

    Button btnEliminar = new Button("Eliminar selección");
    btnEliminar.setPrefSize(220, 60);
    btnEliminar.setStyle("-fx-background-color: #C2C2C2; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");

    Button btnCancelar = new Button("Cancelar");
    btnCancelar.setPrefSize(220, 60);
    btnCancelar.setStyle("-fx-background-color: #C2C2C2; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");

    Button btnAceptar = new Button("Aceptar");
    btnAceptar.setPrefSize(220, 60);
    btnAceptar.setStyle("-fx-background-color: #4A6FDB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

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
    botones.setPadding(new Insets(15, 0, 0, 0)); // 📌 Un poco de espacio encima de los botones

    Region espacio = new Region();
    espacio.setMinHeight(20); // 📌 Espacio mínimo
    espacio.setPrefHeight(40); // 📌 Espacio moderado
    VBox.setVgrow(espacio, Priority.NEVER); // 📌 No se expande infinito

    VBox layout = new VBox(20, titulo, subtitulo, listaSeleccion, espacio, botones);
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


       
