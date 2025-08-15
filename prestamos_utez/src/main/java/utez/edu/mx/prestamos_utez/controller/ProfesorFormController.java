package utez.edu.mx.prestamos_utez.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.IProfesorDao;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.dao.impl.ProfesorImplDao;
import utez.edu.mx.prestamos_utez.dao.impl.AreaImplDao;
import utez.edu.mx.prestamos_utez.model.Division;
import utez.edu.mx.prestamos_utez.model.Area;
import utez.edu.mx.prestamos_utez.model.Profesor;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfesorFormController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Division> cbDivision;
    @FXML private ComboBox<Area> cbArea;              // <— NUEVO
    @FXML private TextField txtProfesor; // apellidos
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Button btnCancelar;

    private final AreaImplDao areaDao = new AreaImplDao();

    private Profesor profesorEditando;
    private ProfesorListController profesorListController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Render “bonito” nombres en combos
        cbDivision.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Division item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cbDivision.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Division item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        cbArea.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cbArea.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        // Si es Encargado, fijar división; si no, cargar todas
        var usuario = utez.edu.mx.prestamos_utez.sesion.AdministradorSesion.getUsuarioActual();
        if (usuario != null && "Encargado".equalsIgnoreCase(usuario.getNombreRol())) {
            cbDivision.getItems().clear();
            cbDivision.getItems().add(new Division(usuario.getIdDivision(), usuario.getNombreDivision()));
            cbDivision.getSelectionModel().selectFirst();
            cbDivision.setDisable(true);
            cargarAreasDeDivision(usuario.getIdDivision());
        } else {
            cargarDivisiones();
            cbDivision.setDisable(false);
        }

        // Cuando cambia la división, recargar áreas
        cbDivision.valueProperty().addListener((obs, oldV, newV) -> {
            cbArea.getItems().clear();
            if (newV != null) {
                cargarAreasDeDivision(newV.getId());
            }
        });
    }

    private void cargarDivisiones() {
        DivisionImpDao dao = new DivisionImpDao();
        cbDivision.getItems().setAll(dao.obtenerDivisiones());
        // if (!cbDivision.getItems().isEmpty()) cbDivision.getSelectionModel().selectFirst();
    }

    private void cargarAreasDeDivision(int idDivision) {
        var lista = areaDao.obtenerPorDivision(idDivision);
        cbArea.getItems().setAll(lista);
         if (!lista.isEmpty()) cbArea.getSelectionModel().selectFirst();
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

        // Seleccionar división y cargar áreas de esa división
        if (cbDivision.getItems() != null && p.getDivision() != null) {
            cbDivision.getItems().stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().equalsIgnoreCase(p.getDivision()))
                    .findFirst()
                    .ifPresent(d -> {
                        cbDivision.setValue(d);
                        cargarAreasDeDivision(d.getId());
                        // seleccionar área por nombre (si lo tienes) o por id
                        if (p.getArea() != null) {
                            cbArea.getItems().stream()
                                    .filter(a -> a.getNombre() != null && a.getNombre().equalsIgnoreCase(p.getArea()))
                                    .findFirst()
                                    .ifPresent(cbArea::setValue);
                        } else if (p.getIdArea() > 0) {
                            cbArea.getItems().stream()
                                    .filter(a -> a.getId() == p.getIdArea())
                                    .findFirst()
                                    .ifPresent(cbArea::setValue);
                        }
                    });
        }
    }

    @FXML
    private void onGuardar() {
        String nombre    = trimOrEmpty(txtNombre.getText());
        String apellidos = trimOrEmpty(txtProfesor.getText());
        String correo    = trimOrEmpty(txtCorreo.getText());
        String telefono  = trimOrEmpty(txtTelefono.getText());
        Division divisionSeleccionada = cbDivision.getValue();
        Area areaSeleccionada = cbArea.getValue();

        if (divisionSeleccionada == null || areaSeleccionada == null ||
                nombre.isBlank() || apellidos.isBlank()) {
            new Alert(Alert.AlertType.WARNING,
                    "Faltan datos obligatorios (nombre, apellidos, división y área).").showAndWait();
            return;
        }

        IProfesorDao dao = new ProfesorImplDao();

        if (profesorEditando == null) {
            Profesor profesor = new Profesor();
            profesor.setNombre(nombre);
            profesor.setApellidos(apellidos);
            profesor.setCorreo(correo);
            profesor.setTelefono(telefono);
            profesor.setIdDivision(divisionSeleccionada.getId());
            profesor.setDivision(divisionSeleccionada.getNombre());
            profesor.setIdArea(areaSeleccionada.getId());         // NUEVO
            profesor.setArea(areaSeleccionada.getNombre());       // NUEVO

            boolean ok = dao.create(profesor);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "No se pudo guardar el profesor.").showAndWait();
                return;
            }
        } else {
            profesorEditando.setNombre(nombre);
            profesorEditando.setApellidos(apellidos);
            profesorEditando.setCorreo(correo);
            profesorEditando.setTelefono(telefono);
            profesorEditando.setIdDivision(divisionSeleccionada.getId());
            profesorEditando.setDivision(divisionSeleccionada.getNombre());
            profesorEditando.setIdArea(areaSeleccionada.getId()); // NUEVO
            profesorEditando.setArea(areaSeleccionada.getNombre());// NUEVO

            boolean ok = dao.update(profesorEditando);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el profesor.").showAndWait();
                return;
            }
        }

        if (profesorListController != null) profesorListController.cargarProfesores();
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }
    private static String trimOrEmpty(String s) { return s == null ? "" : s.trim(); }
}
