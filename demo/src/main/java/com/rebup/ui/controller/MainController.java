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
                try {
                    guardarSeleccionados();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
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

            imageView.setOnMouseClicked(e -> agregarObjeto(tipo, imageBorderPane, minusCircle));
            botonObjeto.setOnAction(e -> agregarObjeto(tipo, imageBorderPane, minusCircle));
            plusCircle.setOnMouseClicked(e -> agregarObjeto(tipo, imageBorderPane, minusCircle));
            minusCircle.setOnMouseClicked(e -> disminuirObjeto(tipo, imageBorderPane, minusCircle));

            contenedor.getChildren().addAll(imageBorderPane, botonObjeto);
            gridEquipos.add(contenedor, columna, fila);

            columna++;
            if (columna == 3) {
                columna = 0;
                fila++;
            }
        }
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

        Label mensaje = new Label("¡Préstamo confirmado!");
        mensaje.setStyle("-fx-font-size: 28px; -fx-text-fill: black;");

        Button btnContinuar = new Button("Continuar");
        btnContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-border-color: black; -fx-padding: 10 40;");
        btnContinuar.setOnAction(e -> {
            alerta.close();
            escenaPrincipal.getRoot().setEffect(null);
        });

        VBox layout = new VBox(40, mensaje, btnContinuar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 20; -fx-background-radius: 20;");

        Scene scene = new Scene(layout, 400, 200);
        alerta.setScene(scene);
        alerta.centerOnScreen();
        alerta.showAndWait();
    }

}
