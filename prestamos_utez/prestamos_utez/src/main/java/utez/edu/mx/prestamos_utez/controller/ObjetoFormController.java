package utez.edu.mx.prestamos_utez.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IObjetoDao;
import utez.edu.mx.prestamos_utez.dao.impl.ObjetoImplDao;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.model.Division;
import utez.edu.mx.prestamos_utez.model.Objeto;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ObjetoFormController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;    // SOLO cantidad (int)
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtInventario;
    @FXML private TextField txtSerie;
    @FXML private TextField txtImagen;

    @FXML private ComboBox<Division> cbDivision;
    @FXML private Button btnCancelar;

    private Objeto objetoEditando;                  // null = crear, !null = editar
    private ObjetoListController objetoListController; // para refrescar la tabla al cerrar

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDivisiones();
    }

    private void cargarDivisiones() {
        try {
            var dao = new DivisionImpDao();                   // Nombre coincide con tu import
            var divisiones = dao.obtenerDivisiones();         // Método ahora existe
            cbDivision.setItems(FXCollections.observableArrayList(divisiones));
            // Si quieres seleccionar la primera:
            // if (!divisiones.isEmpty()) cbDivision.getSelectionModel().select(0);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudieron cargar las divisiones: " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error al cargar divisiones: " + e.getMessage()).showAndWait();
        }
    }

    // ===== API para el caller =====
    public void setObjetoListController(ObjetoListController controller) {
        this.objetoListController = controller;
    }

    public void setObjetoAEditar(Objeto o) {
        this.objetoEditando = o;
        if (o != null) {
            txtNombre.setText(o.getNombre());
            txtCantidad.setText(String.valueOf(o.getCantidad()));
            txtDescripcion.setText(o.getDescripcion());
            txtInventario.setText(o.getNumeroInventario());
            txtSerie.setText(o.getNumeroSerie());
            txtImagen.setText(o.getImagenUrl());

            // seleccionar división por nombre (ajusta si guardas id)
            if (cbDivision.getItems() != null && o.getDivision() != null) {
                cbDivision.getItems().stream()
                        .filter(d -> d.getNombre() != null
                                && d.getNombre().equalsIgnoreCase(o.getDivision()))
                        .findFirst()
                        .ifPresent(cbDivision::setValue);
            }
        }
    }

    // ===== Botones =====
    @FXML
    private void onGuardar(ActionEvent event) {
        String nombre = safe(txtNombre.getText());
        String cantStr = safe(txtCantidad.getText());
        String descripcion = safe(txtDescripcion.getText());
        String urlImagen = safe(txtImagen.getText());
        String numeroSerie = safe(txtSerie.getText());
        String numeroInventario = safe(txtInventario.getText());
        Division divSel = cbDivision.getValue();

        // Validaciones mínimas
        if (nombre.isBlank() || cantStr.isBlank() || divSel == null) {
            alerta(Alert.AlertType.WARNING, "Faltan datos obligatorios (nombre, cantidad, división).");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad < 0) throw new NumberFormatException("Cantidad negativa");
        } catch (NumberFormatException ex) {
            alerta(Alert.AlertType.WARNING, "Cantidad inválida. Debe ser un entero mayor o igual a 0.");
            return;
        }

        IObjetoDao dao = new ObjetoImplDao();

        if (objetoEditando == null) {
            // CREAR
            Objeto obj = new Objeto();
            obj.setNombre(nombre);
            obj.setCantidad(cantidad);
            obj.setDescripcion(descripcion);
            obj.setNumeroInventario(numeroInventario);
            obj.setNumeroSerie(numeroSerie);
            obj.setImagenUrl(urlImagen);

            // Ajusta estas dos líneas a tu modelo/BD:
            obj.setDivision(divSel.getNombre()); // si guardas el nombre
            try { obj.setIdDivision(divSel.getId()); } catch (NoSuchMethodError | Exception ignored) { /* si no existe, omite */ }

            boolean ok = dao.create(obj);
            if (!ok) {
                alerta(Alert.AlertType.ERROR, "No se pudo guardar el objeto.");
                return;
            }
        } else {
            // EDITAR
            objetoEditando.setNombre(nombre);
            objetoEditando.setCantidad(cantidad);
            objetoEditando.setDescripcion(descripcion);
            objetoEditando.setNumeroInventario(numeroInventario);
            objetoEditando.setNumeroSerie(numeroSerie);
            objetoEditando.setImagenUrl(urlImagen);

            objetoEditando.setDivision(divSel.getNombre());
            try { objetoEditando.setIdDivision(divSel.getId()); } catch (NoSuchMethodError | Exception ignored) { /* si no existe, omite */ }

            boolean ok = dao.update(objetoEditando);
            if (!ok) {
                alerta(Alert.AlertType.ERROR, "No se pudo actualizar el objeto.");
                return;
            }
        }

        if (objetoListController != null) {
            objetoListController.cargarObjetos(); // refresca la tabla
        }
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    // ===== Utils =====
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private void alerta(Alert.AlertType t, String msg) {
        new Alert(t, msg, ButtonType.OK).showAndWait();
    }
}
