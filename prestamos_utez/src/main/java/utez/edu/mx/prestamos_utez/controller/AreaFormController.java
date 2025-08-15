package utez.edu.mx.prestamos_utez.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.dao.impl.AreaImplDao;
import utez.edu.mx.prestamos_utez.dao.impl.DivisionImpDao;
import utez.edu.mx.prestamos_utez.model.Area;
import utez.edu.mx.prestamos_utez.model.Division;

public class AreaFormController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Division> cbDivision;

    private final AreaImplDao areaDao = new AreaImplDao();
    private final DivisionImpDao divisionDao = new DivisionImpDao();

    private AreaListController listController;
    private Area aEditar;

    @FXML
    private void initialize() {
        // Carga divisiones (ya no lanza SQLException)
        cbDivision.setItems(FXCollections.observableArrayList(divisionDao.obtenerDivisiones()));

        // Muestra el nombre en el ComboBox
        cbDivision.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Division item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cbDivision.setCellFactory(listView -> new ListCell<>() {
            @Override protected void updateItem(Division item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
    }

    public void setAreaListController(AreaListController c) {
        this.listController = c;
    }

    public void setAreaEditar(Area a) {
        this.aEditar = a;
        txtNombre.setText(a.getNombre());

        // Seleccionar división por id
        if (cbDivision.getItems() != null) {
            cbDivision.getItems().stream()
                    .filter(d -> d.getId() == a.getIdDivision())
                    .findFirst()
                    .ifPresent(cbDivision::setValue);
        }
    }

    @FXML
    private void onGuardar() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        Division d = cbDivision.getValue();

        if (nombre.isBlank() || d == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Nombre y División son obligatorios.",
                    ButtonType.OK).showAndWait();
            return;
        }

        boolean ok;
        if (aEditar == null) {
            Area a = new Area();
            a.setNombre(nombre);
            a.setIdDivision(d.getId());
            ok = areaDao.create(a);
        } else {
            aEditar.setNombre(nombre);
            aEditar.setIdDivision(d.getId());
            ok = areaDao.update(aEditar);
        }

        if (!ok) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo guardar.",
                    ButtonType.OK).showAndWait();
            return;
        }

        if (listController != null) listController.cargarAreas();
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}
