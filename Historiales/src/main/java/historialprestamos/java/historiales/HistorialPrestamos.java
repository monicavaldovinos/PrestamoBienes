package historialprestamos.java.historiales;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class HistorialPrestamos extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Crear el layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Crear el menú lateral
        VBox sidebar = crearSidebar();
        root.setLeft(sidebar);

        // Crear el contenido principal
        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(10));

        Label titulo = new Label("HISTORIAL DE PRÉSTAMOS");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Crear la tabla de préstamos
        TableView<Prestamo> tablaPrestamos = crearTablaPrestamos();

        mainContent.getChildren().addAll(titulo, tablaPrestamos);
        root.setCenter(mainContent);

        // Configurar la escena
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Sistema de Préstamos - Historial");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox crearSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #022E5D; -fx-min-width: 200px;");

        Label titulo = new Label("HISTORIAL");
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button btnProfesores = new Button("Profesores");
        Button btnPrestamos = new Button("Préstamos");
        Button btnDevoluciones = new Button("Devoluciones");
        Button btnHistorial = new Button("Historial");
        Button btnPersonalizar = new Button("Personalizar");
        Button btnCerrarSesion = new Button("Cerrar sesión");

        // Estilizar los botones
        String botonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left;";
        btnProfesores.setStyle(botonStyle);
        btnPrestamos.setStyle(botonStyle);
        btnDevoluciones.setStyle(botonStyle);
        btnHistorial.setStyle(botonStyle + "-fx-background-color: #34495e;");
        btnPersonalizar.setStyle(botonStyle);
        btnCerrarSesion.setStyle(botonStyle);

        // Hacer que los botones ocupen todo el ancho
        btnProfesores.setMaxWidth(Double.MAX_VALUE);
        btnPrestamos.setMaxWidth(Double.MAX_VALUE);
        btnDevoluciones.setMaxWidth(Double.MAX_VALUE);
        btnHistorial.setMaxWidth(Double.MAX_VALUE);
        btnPersonalizar.setMaxWidth(Double.MAX_VALUE);
        btnCerrarSesion.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(titulo, btnProfesores, btnPrestamos,
                btnDevoluciones, btnHistorial,
                btnPersonalizar, btnCerrarSesion);
        return sidebar;
    }

    private TableView<Prestamo> crearTablaPrestamos() {
        TableView<Prestamo> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Crear columnas
        TableColumn<Prestamo, String> colProfesor = new TableColumn<>("Profesor");
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesor"));

        TableColumn<Prestamo, String> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadObjetos"));

        TableColumn<Prestamo, String> colFechaPrestamo = new TableColumn<>("Fecha préstamo");
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));

        TableColumn<Prestamo, String> colFechaEntrega = new TableColumn<>("Fecha entrega");
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));

        TableColumn<Prestamo, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<Prestamo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Pendiente")) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<Prestamo, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<Prestamo, Void>() {
            private final Button btnDetalles = new Button("Ver detalles");
            {
                btnDetalles.setOnAction(event -> {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    mostrarDetallesObjetos(prestamo);
                });
                btnDetalles.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    if (prestamo.getObjetos().size() > 1) {
                        setGraphic(btnDetalles);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tabla.getColumns().addAll(colProfesor, colCantidad, colFechaPrestamo, colFechaEntrega, colEstado, colAcciones);

        // Datos de ejemplo con estados automáticos (¡Copia desde aquí!)
        ObservableList<Prestamo> datos = FXCollections.observableArrayList(
                new Prestamo("Profesor García",
                        Arrays.asList(
                                "Proyector (Serial: PROY-001) ✅ ENTREGADO",
                                "Portátil (Serial: LAP-205) ⏳ PENDIENTE",
                                "Tableta gráfica (Serial: TAB-012) ✅ ENTREGADO"
                        ),
                        "2023-10-15",
                        "2023-10-20",
                        ""), // Estado vacío (se calculará)

                new Prestamo("Profesora Martínez",
                        Arrays.asList("Micrófono (Serial: MIC-022) ✅ ENTREGADO"),
                        "2023-10-18",
                        "2023-10-19",
                        ""),

                new Prestamo("Profesor López",
                        Arrays.asList(
                                "Altavoz (Serial: ALT-103) ⏳ PENDIENTE",
                                "Cámara (Serial: CAM-045) ⏳ PENDIENTE"
                        ),
                        "2023-10-20",
                        "",
                        "")
        );

// Calcula el estado general (¡Esto VA JUSTO DESPUÉS!)
        for (Prestamo p : datos) {
            String estado = p.tieneObjetosPendientes() ? "Pendiente" : "Completado";
            p.estado.set(estado);
        }
// Hasta aquí pega

        tabla.setItems(datos);
        return tabla;
    }

    private void mostrarDetallesObjetos(Prestamo prestamo) {
        Stage dialog = new Stage();
        dialog.setTitle("Detalles de objetos prestados - " + prestamo.getProfesor());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label titulo = new Label("Objetos prestados:");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ListView<String> listaObjetos = new ListView<>();

        // Simulación de estados (¡cámbialo por tus datos reales!)
        ObservableList<String> objetosConEstado = FXCollections.observableArrayList(
                "Proyector (Serial: PROY-001) ✅ ENTREGADO",
                "Cámara (Serial: CAM-045) ⏳ PENDIENTE",
                "Micrófono (Serial: MIC-007) ✅ ENTREGADO"
        );

        listaObjetos.setItems(objetosConEstado);

        // Colores para los estados
        listaObjetos.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("PENDIENTE")) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.GREEN);
                    }
                }
            }
        });

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> dialog.close());
        btnCerrar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        vbox.getChildren().addAll(titulo, listaObjetos, btnCerrar);
        Scene scene = new Scene(vbox, 350, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    public static class Prestamo {
        private final SimpleStringProperty profesor;
        private final List<String> objetos;
        private final SimpleStringProperty fechaPrestamo;
        private final SimpleStringProperty fechaEntrega;
        private final SimpleStringProperty estado;

        // Método para verificar si hay objetos pendientes
        public boolean tieneObjetosPendientes() {
            // Asumiré que los objetos pendientes tienen "PENDIENTE" en su descripción
            // (Si usas otro sistema, dime para ajustarlo)
            for (String objeto : this.objetos) {
                if (objeto.contains("PENDIENTE")) {
                    return true;
                }
            }
            return false;
        }

        public Prestamo(String profesor, List<String> objetos, String fechaPrestamo, String fechaEntrega, String estado) {
            this.profesor = new SimpleStringProperty(profesor);
            this.objetos = objetos;
            this.fechaPrestamo = new SimpleStringProperty(fechaPrestamo);
            this.fechaEntrega = new SimpleStringProperty(fechaEntrega);
            this.estado = new SimpleStringProperty(estado);
        }

        // Getters
        public String getProfesor() { return profesor.get(); }
        public List<String> getObjetos() { return objetos; }
        public String getCantidadObjetos() { return objetos.size() + " objeto" + (objetos.size() != 1 ? "s" : ""); }
        public String getFechaPrestamo() { return fechaPrestamo.get(); }
        public String getFechaEntrega() { return fechaEntrega.get(); }
        public String getEstado() { return estado.get(); }

        // Property getters
        public SimpleStringProperty profesorProperty() { return profesor; }
        public SimpleStringProperty cantidadObjetosProperty() { return new SimpleStringProperty(getCantidadObjetos()); }
        public SimpleStringProperty fechaPrestamoProperty() { return fechaPrestamo; }
        public SimpleStringProperty fechaEntregaProperty() { return fechaEntrega; }
        public SimpleStringProperty estadoProperty() { return estado; }
    }
}