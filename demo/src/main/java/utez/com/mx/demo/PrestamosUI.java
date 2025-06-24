package utez.com.mx.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PrestamosUI {

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
                    new PrestamosUI().mostrar();
                    ((Stage) btn.getScene().getWindow()).close();
                });
            }
        }

        // Contenido central
        Label titulo = new Label("REGISTRAR PRÉSTAMO");
        titulo.setId("section-title");

        ComboBox<String> comboProfesores = new ComboBox<>();
        comboProfesores.getItems().addAll("Prof. García", "Prof. López", "Prof. Martínez");
        comboProfesores.setPromptText("Selecciona un profesor");

        ObservableList<String> objetosDisponibles = FXCollections.observableArrayList(
                "Microscopio #001", "Proyector #002", "Tablet #003", "Cámara #004", "Multímetro #005"
        );
        FilteredList<String> objetosFiltrados = new FilteredList<>(objetosDisponibles, p -> true);

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar objeto...");
        campoBusqueda.setPrefWidth(280);

        campoBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            objetosFiltrados.setPredicate(obj -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();
                return obj.toLowerCase().contains(filtro);
            });
        });

        ListView<String> listaObjetos = new ListView<>(objetosFiltrados);
        listaObjetos.setPrefHeight(120);

        Button confirmar = new Button("Confirmar préstamo");
        confirmar.setDisable(true);

        comboProfesores.setOnAction(e -> validar(comboProfesores, listaObjetos, confirmar));
        listaObjetos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            validar(comboProfesores, listaObjetos, confirmar);
        });

        VBox formulario = new VBox(12,
                titulo,
                comboProfesores,
                new Label("Buscar objeto:"),
                campoBusqueda,
                listaObjetos,
                confirmar
        );
        formulario.setPadding(new Insets(25));
        formulario.setAlignment(Pos.TOP_LEFT);
        formulario.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        formulario.getStyleClass().add("formulario-contenedor");
        formulario.getStyleClass().add("formulario-prestamos");//estilos


        VBox centerPane = new VBox(formulario);
        centerPane.setPadding(new Insets(30));
        centerPane.setStyle("-fx-background-color: #f0f2f5;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Préstamos");
        stage.setScene(scene);
        stage.show();
    }

    private void validar(ComboBox<String> combo, ListView<String> lista, Button boton) {
        boolean profesorSeleccionado = combo.getValue() != null;
        boolean objetoSeleccionado = lista.getSelectionModel().getSelectedItem() != null;
        boton.setDisable(!(profesorSeleccionado && objetoSeleccionado));
    }
}

