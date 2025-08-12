package utez.edu.mx.prestamos_utez.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.model.Division;
import utez.edu.mx.prestamos_utez.model.Profesor;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfesorFormController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Division> cbDivision;
    @FXML private TextField txtProfesor; // apellidos
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Button btnCancelar;

    private Profesor profesorEditando;
    private ProfesorListController profesorListController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var usuario = utez.edu.mx.prestamos_utez.sesion.AdministradorSesion.getUsuarioActual();
        if (usuario != null && "Encargado".equalsIgnoreCase(usuario.getNombreRol())) {
            // Solo mostrar la división del encargado y deshabilitar el ComboBox
            cbDivision.getItems().clear();
            cbDivision.getItems().add(new Division(usuario.getIdDivision(), usuario.getNombreDivision()));
            cbDivision.getSelectionModel().selectFirst();
            cbDivision.setDisable(true);
        } else {
            cargarDivisiones();
            cbDivision.setDisable(false);
        }
    }

    private void cargarDivisiones() {
        try {
            DivisionImpDao dao = new DivisionImpDao();
            cbDivision.getItems().setAll(dao.obtenerDivisiones());
            // if (!cbDivision.getItems().isEmpty()) cbDivision.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudieron cargar las divisiones: " + e.getMessage()).showAndWait();
        }
    }

    // Para refrescar tabla al guardar
    public void setProfesorListController(ProfesorListController controller) {
        this.profesorListController = controller;
    }

    public void setProfesorAEditar(Profesor p) {
        this.profesorEditando = p;
        if (p == null) return;

        txtNombre.setText(nullSafe(p.getNombre()));
        txtProfesor.setText(nullSafe(p.getApellidos()));
        txtCorreo.setText(nullSafe(p.getCorreo()));
        txtTelefono.setText(nullSafe(p.getTelefono()));

        // seleccionar división en el combo (por nombre)
        if (cbDivision.getItems() != null && p.getDivision() != null) {
            cbDivision.getItems().stream()
                    .filter(d -> d.getNombre() != null
                            && d.getNombre().equalsIgnoreCase(p.getDivision()))
                    .findFirst()
                    .ifPresent(cbDivision::setValue);
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        String nombre    = trimOrEmpty(txtNombre.getText());
        String apellidos = trimOrEmpty(txtProfesor.getText());
        String correo    = trimOrEmpty(txtCorreo.getText());
        String telefono  = trimOrEmpty(txtTelefono.getText());
        Division divisionSeleccionada = cbDivision.getValue();

        if (divisionSeleccionada == null || nombre.isBlank() || apellidos.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Faltan datos obligatorios (nombre, apellidos, división).").showAndWait();
            return;
        }

        IProfesorDao dao = new ProfesorImplDao();

        if (profesorEditando == null) {
            // CREAR
            Profesor profesor = new Profesor();
            profesor.setNombre(nombre);
            profesor.setApellidos(apellidos);
            profesor.setCorreo(correo);
            profesor.setTelefono(telefono);
            profesor.setIdDivision(divisionSeleccionada.getId());   // FK
            profesor.setDivision(divisionSeleccionada.getNombre()); // si almacenan nombre también

            boolean ok = dao.create(profesor);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "No se pudo guardar el profesor.").showAndWait();
                return;
            }
        } else {
            // EDITAR
            profesorEditando.setNombre(nombre);
            profesorEditando.setApellidos(apellidos);
            profesorEditando.setCorreo(correo);
            profesorEditando.setTelefono(telefono);
            profesorEditando.setIdDivision(divisionSeleccionada.getId());
            profesorEditando.setDivision(divisionSeleccionada.getNombre());

            boolean ok = dao.update(profesorEditando);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el profesor.").showAndWait();
                return;
            }
        }

        if (profesorListController != null) {
            profesorListController.cargarProfesores();
        }
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }
    private static String trimOrEmpty(String s) { return s == null ? "" : s.trim(); }
}
