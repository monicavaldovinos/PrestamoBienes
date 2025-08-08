package utez.edu.mx.prestamos_utez.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.model.Profesor;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import utez.edu.mx.prestamos_utez.model.Division;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;


public class ProfesorFormController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Division> cbDivision;

    @FXML
    private TextField txtProfesor;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDivisiones();

    }
    private Profesor profesorEditando;

    private void cargarDivisiones() {
        DivisionImpDao dao = new DivisionImpDao();
        cbDivision.getItems().addAll(dao.obtenerDivisiones());
    }
    private ProfesorListController profesorListController;

    public void setProfesorListController(ProfesorListController controller) {
        this.profesorListController = controller;
    }
    public void setProfesorAEditar(Profesor p) {
        this.profesorEditando = p;
        // precargar campos
        txtNombre.setText(p.getNombre());
        txtProfesor.setText(p.getApellidos());
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        // seleccionar división en el combo
        if (cbDivision.getItems() != null) {
            cbDivision.getItems().stream()
                    .filter(d -> d.getNombre().equalsIgnoreCase(p.getDivision()))
                    .findFirst()
                    .ifPresent(cbDivision::setValue);
        }
    }
    @FXML
    private void onGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String apellidos = txtProfesor.getText();
        String correo = txtCorreo.getText();
        String telefono = txtTelefono.getText();
        Division divisionSeleccionada = cbDivision.getValue();

        if (divisionSeleccionada == null || nombre.isBlank() || apellidos.isBlank()) {
            System.out.println("Faltan datos obligatorios");
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
            profesor.setIdDivision(divisionSeleccionada.getId());
            profesor.setDivision(divisionSeleccionada.getNombre());

            boolean ok = dao.create(profesor);
            if (!ok) {
                System.out.println("No se pudo guardar el profesor");
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
                System.out.println("No se pudo actualizar el profesor");
                return;
            }
        }

        if (profesorListController != null) {
            profesorListController.cargarProfesores();
        }
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }




}

