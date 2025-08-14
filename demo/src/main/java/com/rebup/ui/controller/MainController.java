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

    // ===== COMBOBOX PROFESOR =====
    var daoProfesor = new com.rebup.dao.ProfesorDao();
    var dataProfesor = FXCollections.observableArrayList(daoProfesor.listar());

    var filtrado = new FilteredList<>(dataProfesor, p -> true);
    cbProfesor.setItems(filtrado);
    cbProfesor.setEditable(true);

    cbProfesor.setConverter(new javafx.util.StringConverter<com.rebup.model.Profesor>() {
        @Override
        public String toString(com.rebup.model.Profesor profesor) {
            return profesor != null ? profesor.getNombre() : "";
        }
        @Override
        public com.rebup.model.Profesor fromString(String string) {
            if (string == null) return null;
            return dataProfesor.stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase(string))
                    .findFirst()
                    .orElse(null);
        }
    });

    cbProfesor.getEditor().textProperty().addListener((obs, oldTxt, newTxt) -> {
        String q = (newTxt == null) ? "" : newTxt.toLowerCase();
        filtrado.setPredicate(p -> p.getNombre().toLowerCase().contains(q));
        if (!cbProfesor.isShowing()) cbProfesor.show();
    });

    cbProfesor.showingProperty().addListener((obs, wasShowing, isShowing) -> {
        if (isShowing) {
            filtrado.setPredicate(p -> true);
        }
    });

    cbProfesor.setOnAction(e -> {
        var sel = cbProfesor.getValue();
        if (sel != null) {
            System.out.println("Profesor seleccionado -> id=" + sel.getId() + ", nombre=" + sel.getNombre());
        }
    });

    // ===== LOGO =====
    Image img = new Image(getClass().getResourceAsStream("/com/rebup/images/rebup.png"));
    logo.setImage(img);

    // ===== ICONO PRESTAMOS =====
    Image iconPrestamos = new Image(getClass().getResourceAsStream("/com/rebup/images/prestamos.png"));
    ImageView ivPrestamos = new ImageView(iconPrestamos);
    ivPrestamos.setFitWidth(20);
    ivPrestamos.setFitHeight(20);
    btnPrestamos.setGraphic(ivPrestamos);

    // ===== ICONO DEVOLUCIONES =====
    Image iconDevoluciones = new Image(getClass().getResourceAsStream("/com/rebup/images/devoluciones.png"));
    ImageView ivDevoluciones = new ImageView(iconDevoluciones);
    ivDevoluciones.setFitWidth(20);
    ivDevoluciones.setFitHeight(20);
    btnDevoluciones.setGraphic(ivDevoluciones);

    // ===== COMBO FILTRO =====
    comboFiltro.getItems().addAll("Todos", "Disponible", "No disponible");
    comboFiltro.setValue("Todos");
    comboFiltro.setOnAction(e -> mostrarEquipos(comboFiltro.getValue()));
    comboFiltro.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 5; -fx-border-radius: 5;");

    // ===== BOTÓN CONFIRMAR =====
    btnConfirmar.setDisable(true);
    btnConfirmar.setOnAction(e -> {
        if (!seleccionados.isEmpty()) {
            mostrarResumenSeleccion();
        }
    });

    // ===== CARGA INICIAL =====
    mostrarEquipos("Todos");
    actualizarContador();
}


        

    private Map<Integer, Integer> maxCantidadesPorObjeto = new HashMap<>();
    private int paginaActual = 0;
    private final int OBJETOS_POR_PAGINA = 6;

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

        int inicio = paginaActual * OBJETOS_POR_PAGINA;
        int fin = Math.min(inicio + OBJETOS_POR_PAGINA, objetosFiltrados.size());
        List<Objeto> paginaObjetos = objetosFiltrados.subList(inicio, fin);

        int columna = 0, fila = 0;

        for (Objeto obj : paginaObjetos) {
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

        Button btnAnterior = new Button("←");
        btnAnterior.setOnAction(e -> {
            if (paginaActual > 0) {
                paginaActual--;
                mostrarEquipos(filtro);
            }
        });

        Button btnSiguiente = new Button("→");
        btnSiguiente.setOnAction(e -> {
            if ((paginaActual + 1) * OBJETOS_POR_PAGINA < objetosFiltrados.size()) {
                paginaActual++;
                mostrarEquipos(filtro);
            }
        });

        HBox paginacion = new HBox(10, btnAnterior, btnSiguiente);
        paginacion.setAlignment(Pos.CENTER);

        gridEquipos.add(paginacion, 0, fila + 1, 3, 1);
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
        botones.setPadding(new Insets(8, 0, 0, 0)); 

        Region espacio = new Region();
        espacio.setMinHeight(20); 
        espacio.setPrefHeight(40); 
        VBox.setVgrow(espacio, Priority.NEVER); 

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

    
        ImageView ivCheck = null;
        try {
            Image checkImg = new Image(getClass().getResourceAsStream("/com/rebup/images/check.png"));
            ivCheck = new ImageView(checkImg);
            ivCheck.setFitWidth(100); 
            ivCheck.setFitHeight(100);
            ivCheck.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("⚠ No se encontró check.png en /com/rebup/images/");
        }

        Label mensaje = new Label("¡Préstamo confirmado!");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black;");

        VBox vboxMensaje = new VBox(15);
        vboxMensaje.setAlignment(Pos.CENTER);
        if (ivCheck != null) {
            vboxMensaje.getChildren().add(ivCheck);
        }
        vboxMensaje.getChildren().add(mensaje);

        Button btnContinuar = new Button("Continuar");
        btnContinuar.setStyle(
            "-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-border-color: black; -fx-padding: 10 40;"
        );
        btnContinuar.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        VBox layout = new VBox(30, vboxMensaje, btnContinuar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

    
        Scene scene = new Scene(layout, 450, 350);
        alerta.setScene(scene);
        alerta.centerOnScreen();
        alerta.showAndWait();
    }
    }
