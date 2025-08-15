package utez.edu.mx.prestamos_utez.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utez.edu.mx.prestamos_utez.dao.IObjetoDao;
import utez.edu.mx.prestamos_utez.dao.impl.AreaImplDao;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.dao.impl.ObjetoImplDao;
import utez.edu.mx.prestamos_utez.model.*;
import utez.edu.mx.prestamos_utez.sesion.AdministradorSesion;

import java.net.URL;
// import java.sql.SQLException;  // <- ELIMINADO porque no se usa
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ObjetoFormController implements Initializable {

    @FXML private GridPane formPane;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;   // descripción obligatoria
    @FXML private TextField txtImagen;
    @FXML private ComboBox<Division> cbDivision;
    @FXML private TextField txtCantidad;

    // Toggle + bloque por UNIDAD
    @FXML private CheckBox  chkCapturarUnidades;
    @FXML private ScrollPane spUnidades;
    @FXML private VBox      boxUnidades;

    // Bloque por ÁREAS (lote)
    @FXML private VBox  boxDistribucionWrapper;
    @FXML private VBox  boxFilasAreas;
    @FXML private Button btnAgregarFilaArea;

    @FXML private Button btnCancelar;

    private Objeto objetoEditando;                     // null = crear
    private ObjetoListController objetoListController; // para refrescar lista

    private final AreaImplDao areaDao = new AreaImplDao();

    /** ObservableList compartida para que TODOS los ComboBox de área se actualicen al cambiar división. */
    private final ObservableList<Area> areasDivisionActual = FXCollections.observableArrayList();

    private int restanteCache = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicia el bloque de unidades oculto
        spUnidades.setVisible(false);
        spUnidades.setManaged(false);

        // 1) Cargar divisiones (si es Encargado, se fija aquí mismo)
        cargarDivisionesYFijarSiEncargado();

        // 2) Cargar áreas de la división actual
        cargarAreasPorDivision();

        // 3) Arranca con UNA fila de áreas si no hay todavía
        if (boxFilasAreas.getChildren().isEmpty()) {
            agregarFilaArea();
        }

        // Al cambiar de división, recarga las áreas (la UI de filas NO se elimina)
        cbDivision.valueProperty().addListener((obs, oldV, newV) -> {
            cargarAreasPorDivision();   // actualiza la ObservableList => todos los combos se actualizan
            recomputarRestante();       // solo cache; no se muestra
        });

        // Mostrar/ocultar secciones sin borrar lo capturado
        chkCapturarUnidades.selectedProperty().addListener((obs, was, isNow) -> {
            // Unidades
            spUnidades.setVisible(isNow);
            spUnidades.setManaged(isNow);
            if (isNow) {
                regenerarFilasUnidad(txtCantidad.getText()); // genera o regenera inputs por unidad
            }

            // Áreas (lote)
            boxDistribucionWrapper.setVisible(!isNow);
            boxDistribucionWrapper.setManaged(!isNow);

            // OJO: No limpiamos ni áreas ni unidades, solo ocultamos/mostramos
        });

        // Al cambiar la cantidad, regeneramos solo si está en modo UNIDAD
        txtCantidad.textProperty().addListener((obs, old, val) -> {
            if (chkCapturarUnidades.isSelected()) {
                regenerarFilasUnidad(val);
            }
            recomputarRestante();
        });

        // Botón agregar fila de área
        btnAgregarFilaArea.setOnAction(e -> agregarFilaArea());

        recomputarRestante();
    }

    /* ====== soporte para caller ====== */
    public void setObjetoListController(ObjetoListController controller) {
        this.objetoListController = controller;
    }

    public void setObjetoAEditar(Objeto o) {
        this.objetoEditando = o;
        if (o == null) return;

        txtNombre.setText(nz(o.getNombre()));
        txtDescripcion.setText(nz(o.getDescripcion()));
        txtImagen.setText(nz(o.getImagenUrl()));
        txtCantidad.setText(String.valueOf(o.getCantidad()));

        // seleccionar división
        if (cbDivision.getItems() != null) {
            if (o.getIdDivision() > 0) {
                cbDivision.getItems().stream()
                        .filter(d -> d.getId() == o.getIdDivision())
                        .findFirst()
                        .ifPresent(cbDivision::setValue);
            } else if (o.getDivision() != null) {
                cbDivision.getItems().stream()
                        .filter(d -> d.getNombre() != null && d.getNombre().equalsIgnoreCase(o.getDivision()))
                        .findFirst()
                        .ifPresent(cbDivision::setValue);
            }
        }

        // En edición: por defecto mostramos ÁREAS (no re-editamos ítems aquí)
        chkCapturarUnidades.setSelected(false);
        spUnidades.setVisible(false);
        spUnidades.setManaged(false);
        boxDistribucionWrapper.setVisible(true);
        boxDistribucionWrapper.setManaged(true);

        // Recargar áreas para la división ya fijada
        cargarAreasPorDivision();

        recomputarRestante();
    }

    /* ====== divisiones & áreas ====== */

    // <- CORREGIDO: sin try/catch SQLException
    private void cargarDivisionesYFijarSiEncargado() {
        var dao = new DivisionImpDao();
        var divisiones = dao.obtenerDivisiones();
        cbDivision.setItems(FXCollections.observableArrayList(divisiones));

        // Si es Encargado, fijar su división en el combo
        Usuario u = AdministradorSesion.getUsuarioActual();
        if (u != null && "Encargado".equalsIgnoreCase(u.getNombreRol()) && u.getIdDivision() > 0) {
            cbDivision.getItems().stream()
                    .filter(d -> d.getId() == u.getIdDivision())
                    .findFirst()
                    .ifPresent(cbDivision::setValue);
            cbDivision.setDisable(true);
        } else {
            cbDivision.setDisable(false);
        }
    }

    /** Devuelve el id de división a usar: el seleccionado, o el del Encargado si aplica. */
    private Integer idDivisionActual() {
        Division d = cbDivision.getValue();
        if (d != null) return d.getId();

        Usuario u = AdministradorSesion.getUsuarioActual();
        if (u != null && "Encargado".equalsIgnoreCase(u.getNombreRol()) && u.getIdDivision() > 0) {
            return u.getIdDivision();
        }
        return null;
    }

    /** Rellena la ObservableList con las áreas de la división actual y registra en logs. */
    private void cargarAreasPorDivision() {
        Integer idDiv = idDivisionActual();

        if (idDiv == null) {
            System.out.println("[ÁREAS] No hay división seleccionada aún.");
            areasDivisionActual.clear();
            return;
        }

        List<Area> desdeBd = areaDao.obtenerPorDivision(idDiv);

        System.out.println("[ÁREAS] División seleccionada: " + idDiv +
                "  -> áreas recuperadas: " + (desdeBd == null ? 0 : desdeBd.size()));
        if (desdeBd != null) {
            for (Area a : desdeBd) {
                System.out.println("   - " + a.getId() + " | " + a.getNombre());
            }
        }
        // Actualiza TODOS los combos que tengan setItems(areasDivisionActual)
        areasDivisionActual.setAll(desdeBd == null ? List.of() : desdeBd);
    }

    /* ====== UNIDADES (dinámico) ====== */

    private void regenerarFilasUnidad(String val) {
        int n = 0;
        try { n = Math.max(0, Integer.parseInt(val.trim())); } catch (Exception ignored) {}

        boxUnidades.getChildren().clear();
        for (int i = 1; i <= n; i++) boxUnidades.getChildren().add(crearFilaUnidad(i));
    }

    private HBox crearFilaUnidad(int index) {
        var fila  = new HBox(8);
        fila.setPadding(new Insets(2));

        var lbl   = new Label("#" + index);
        var tfInv = new TextField(); tfInv.setPromptText("Inventario (" + index + ")");
        var tfSer = new TextField(); tfSer.setPromptText("Serie (" + index + ")");

        HBox.setHgrow(tfInv, Priority.ALWAYS);
        HBox.setHgrow(tfSer, Priority.ALWAYS);

        fila.getProperties().put("tfInv", tfInv);
        fila.getProperties().put("tfSer", tfSer);
        fila.getChildren().addAll(lbl, tfInv, tfSer);
        return fila;
    }

    private List<ObjetoItem> recogerItemsPorUnidad() {
        List<ObjetoItem> items = new ArrayList<>();
        for (var node : boxUnidades.getChildren()) {
            if (node instanceof HBox fila) {
                var tfInv = (TextField) fila.getProperties().get("tfInv");
                var tfSer = (TextField) fila.getProperties().get("tfSer");

                var it = new ObjetoItem();
                it.setNumeroInventario(nz(tfInv.getText()));
                it.setNumeroSerie(nz(tfSer.getText()));
                it.setEstado("DISPONIBLE");
                items.add(it);
            }
        }
        return items;
    }

    /* ====== ÁREAS (dinámico) ====== */

    private void agregarFilaArea() {
        FilaArea fila = new FilaArea();
        boxFilasAreas.getChildren().add(fila.getRoot());
    }

    private int getCantidadTotal() {
        try { return Integer.parseInt(nz(txtCantidad.getText())); }
        catch (Exception e) { return 0; }
    }

    private int sumarFilasAreas() {
        int suma = 0;
        for (var n : boxFilasAreas.getChildren()) {
            if (n.getUserData() instanceof FilaArea f) {
                suma += f.getCantidad();
            }
        }
        return suma;
    }

    private void recomputarRestante() {
        int total = getCantidadTotal();
        int suma  = sumarFilasAreas();
        restanteCache = total - suma;
    }

    private int getRestante() { return restanteCache; }

    private List<ObjetoArea> colectarDistribucionAreas() {
        List<ObjetoArea> lista = new ArrayList<>();
        for (var n : boxFilasAreas.getChildren()) {
            if (n.getUserData() instanceof FilaArea f) {
                Area a = f.getArea();
                int  c = f.getCantidad();
                if (a != null && c > 0) {
                    ObjetoArea oa = new ObjetoArea();
                    oa.setIdArea(a.getId());
                    oa.setCantidad(c);
                    lista.add(oa);
                }
            }
        }
        return lista;
    }

    private class FilaArea {
        private final HBox root    = new HBox(8);
        private final ComboBox<Area> cbArea = new ComboBox<>();
        private final TextField txtCant     = new TextField();
        private final Button btnQuitar      = new Button("Quitar");

        FilaArea() {
            // Combo de áreas: ENLAZADO a la ObservableList compartida
            cbArea.setPrefWidth(260);
            cbArea.setConverter(new StringConverter<>() {
                @Override public String toString(Area a) { return a == null ? "" : a.getNombre(); }
                @Override public Area fromString(String s) { return null; }
            });
            cbArea.setItems(areasDivisionActual);
            cbArea.setPromptText("Seleccione un área");

            // Cantidad (solo enteros)
            txtCant.setPromptText("Cantidad");
            txtCant.setPrefWidth(100);
            txtCant.textProperty().addListener((obs, old, val) -> {
                if (!val.matches("\\d*")) {
                    txtCant.setText(val.replaceAll("[^\\d]", "")); // solo números
                }
                recomputarRestante();
            });

            // Quitar fila
            btnQuitar.setOnAction(e -> {
                boxFilasAreas.getChildren().remove(root);
                recomputarRestante();
            });

            root.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().addAll(new Label("Área:"), cbArea,
                    new Label("Cantidad:"), txtCant, btnQuitar);

            root.setUserData(this);
        }

        public HBox getRoot()   { return root; }
        public Area getArea()   { return cbArea.getValue(); }
        public int  getCantidad(){
            try { return Integer.parseInt(nz(txtCant.getText())); }
            catch (Exception e) { return 0; }
        }
    }

    /* ====== Guardar / Cancelar ====== */

    @FXML
    private void onGuardar() {
        String nombre      = nz(txtNombre.getText());
        String descripcion = nz(txtDescripcion.getText()); // descripción obligatoria
        String urlImagen   = nz(txtImagen.getText());
        Division divSel    = cbDivision.getValue();

        // división efectiva (si es Encargado, fija su división)
        Usuario u = AdministradorSesion.getUsuarioActual();
        int idDivision; String nombreDiv;
        if (u != null && "Encargado".equalsIgnoreCase(u.getNombreRol()) && u.getIdDivision() > 0) {
            idDivision = u.getIdDivision();
            nombreDiv  = (cbDivision.getItems() == null) ? "" :
                    cbDivision.getItems().stream().filter(d -> d.getId() == idDivision)
                            .map(Division::getNombre).findFirst().orElse("");
        } else {
            if (divSel == null) {
                alerta(Alert.AlertType.WARNING, "Selecciona la división.");
                return;
            }
            idDivision = divSel.getId();
            nombreDiv  = divSel.getNombre();
        }

        // cantidad
        int cantidad;
        try {
            cantidad = Integer.parseInt(nz(txtCantidad.getText()));
            if (cantidad < 0) throw new NumberFormatException("negativa");
        } catch (NumberFormatException ex) {
            alerta(Alert.AlertType.WARNING, "Cantidad inválida. Debe ser un entero ≥ 0.");
            return;
        }

        if (nombre.isBlank()) {
            alerta(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return;
        }
        if (descripcion.isBlank()) {
            alerta(Alert.AlertType.WARNING, "La descripción es obligatoria.");
            return;
        }

        // armar objeto
        Objeto obj = (objetoEditando == null) ? new Objeto() : objetoEditando;
        obj.setNombre(nombre);
        obj.setDescripcion(descripcion);
        obj.setImagenUrl(urlImagen);
        obj.setCantidad(cantidad);
        obj.setIdDivision(idDivision);
        obj.setDivision(nombreDiv);

        IObjetoDao dao = new ObjetoImplDao();
        boolean ok;

        if (chkCapturarUnidades.isSelected()) {
            // modo por unidad
            if (boxUnidades.getChildren().size() != cantidad) {
                alerta(Alert.AlertType.WARNING, "Debes capturar las " + cantidad + " unidades.");
                return;
            }
            List<ObjetoItem> items = recogerItemsPorUnidad();
            if (items.size() != cantidad) {
                alerta(Alert.AlertType.WARNING, "El número de ítems debe coincidir con la cantidad.");
                return;
            }
            ok = (objetoEditando == null)
                    ? dao.create(obj, items)
                    : dao.update(obj);
        } else {
            // modo por áreas (lote)
            recomputarRestante();
            int restante = getRestante();
            if (restante != 0) {
                alerta(Alert.AlertType.WARNING, "Debes distribuir exactamente la cantidad total. Restante: " + restante);
                return;
            }
            List<ObjetoArea> distribucion = colectarDistribucionAreas();
            if (distribucion.isEmpty()) {
                alerta(Alert.AlertType.WARNING, "Agrega al menos una fila de área con cantidad > 0.");
                return;
            }
            ok = (objetoEditando == null)
                    ? dao.create(obj, new ArrayList<>(), distribucion)
                    : dao.update(obj);
        }

        if (!ok) {
            alerta(Alert.AlertType.ERROR, "No se pudo guardar la información.");
            return;
        }

        if (objetoListController != null) objetoListController.cargarObjetos();
        cerrarVentana();
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    /* ====== utils ====== */
    private static String nz(String s) { return s == null ? "" : s.trim(); }

    private void alerta(Alert.AlertType t, String msg) {
        new Alert(t, msg, ButtonType.OK).showAndWait();
    }
}
