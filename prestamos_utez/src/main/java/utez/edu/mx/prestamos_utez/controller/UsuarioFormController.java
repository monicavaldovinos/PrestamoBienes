package utez.edu.mx.prestamos_utez.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.config.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsuarioFormController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbRol; // mostramos nombre del rol
    @FXML private PasswordField txtPass;
    @FXML private Label lblDivision;
    @FXML private ComboBox<String> cmbDivision;

    private final Map<String, Integer> rolMap = new LinkedHashMap<>();
    private final Map<String, Integer> divisionMap = new LinkedHashMap<>(); // nombre -> id

    private Integer editIdUsuario = null; // null = crear; no-null = editar
    private Runnable onSaved;             // callback para refrescar lista

    public void initialize() {
        cargarRoles();
        cmbRol.valueProperty().addListener((obs, old, nuevo) -> {
            boolean esEncargado = "Encargado".equals(nuevo);
            lblDivision.setVisible(esEncargado);
            lblDivision.setManaged(esEncargado);
            cmbDivision.setVisible(esEncargado);
            cmbDivision.setManaged(esEncargado);
            if (esEncargado) {
                cargarDivisiones();
            } else {
                cmbDivision.getItems().clear();
            }
        });
    }

    /** Llamar antes de mostrar el form si vas a EDITAR */
    public void setUsuario(Integer idUsuario, String nombre, String apellidos, String correo,
                           Integer idRol, String nombreRol, Runnable onSaved) {
        this.editIdUsuario = idUsuario;
        this.onSaved = onSaved;

        if (idUsuario != null) {
            txtNombre.setText(nombre);
            txtApellidos.setText(apellidos);
            txtCorreo.setText(correo);
            if (nombreRol != null) {
                cmbRol.setValue(nombreRol);
            } else if (idRol != null) {
                rolMap.forEach((nom, id) -> { if (id.equals(idRol)) cmbRol.setValue(nom); });
            }
            // contraseña vacía => no se actualiza
        } else {
            cmbRol.getSelectionModel().clearSelection();
        }
    }

    private void cargarDivisiones() {
        divisionMap.clear();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ID_DIVISION, NOMBRE FROM DIVISION ORDER BY NOMBRE ASC");
             ResultSet rs = ps.executeQuery()) {
            var lista = FXCollections.<String>observableArrayList();
            while (rs.next()) {
                divisionMap.put(rs.getString("NOMBRE"), rs.getInt("ID_DIVISION"));
                lista.add(rs.getString("NOMBRE"));
            }
            cmbDivision.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace(); // sin pop-ups
        }
    }

    @FXML
    private void cancelar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank() || txtApellidos.getText().isBlank()
                || txtCorreo.getText().isBlank() || cmbRol.getValue() == null) {
            alerta(Alert.AlertType.WARNING, "Completa nombre, apellidos, correo y rol.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        Integer idRol = rolMap.get(cmbRol.getValue());
        String pass = txtPass.getText(); // si vacío en editar, no se actualiza

        Integer idDivision = null;
        if ("Encargado".equals(cmbRol.getValue())) {
            if (cmbDivision.getValue() == null || cmbDivision.getValue().isEmpty()) {
                alerta(Alert.AlertType.WARNING, "Selecciona la división para el encargado.");
                return;
            }
            idDivision = divisionMap.get(cmbDivision.getValue());
        }

        try (Connection con = DBConnection.getConnection()) {
            if (editIdUsuario == null) {
                // CREAR (silencioso)
                if (pass == null || pass.isBlank()) {
                    alerta(Alert.AlertType.WARNING, "Define una contraseña para el nuevo usuario.");
                    return;
                }
                String sql = """
                    INSERT INTO USUARIO (ID_USUARIO, NOMBRE, APELLIDOS, CORREO, CONTRASENA, ID_ROL, ID_DIVISION)
                    VALUES (USUARIO_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)
                """;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, apellidos);
                    ps.setString(3, correo);
                    ps.setString(4, pass);
                    ps.setInt(5, idRol);
                    if (idDivision != null) ps.setInt(6, idDivision); else ps.setNull(6, Types.INTEGER);
                    int n = ps.executeUpdate();
                    if (n == 1) {
                        if (onSaved != null) onSaved.run(); // refresca lista
                        cancelar(); // cierra sin pop-up
                    } else {
                        alerta(Alert.AlertType.ERROR, "No se pudo crear el usuario.");
                    }
                }
            } else {
                // EDITAR (silencioso)
                String sqlSinPass = """
                    UPDATE USUARIO
                    SET NOMBRE=?, APELLIDOS=?, CORREO=?, ID_ROL=?, ID_DIVISION=?
                    WHERE ID_USUARIO=?
                """;
                String sqlConPass = """
                    UPDATE USUARIO
                    SET NOMBRE=?, APELLIDOS=?, CORREO=?, ID_ROL=?, CONTRASENA=?, ID_DIVISION=?
                    WHERE ID_USUARIO=?
                """;
                if (pass == null || pass.isBlank()) {
                    try (PreparedStatement ps = con.prepareStatement(sqlSinPass)) {
                        ps.setString(1, nombre);
                        ps.setString(2, apellidos);
                        ps.setString(3, correo);
                        ps.setInt(4, idRol);
                        if (idDivision != null) ps.setInt(5, idDivision); else ps.setNull(5, Types.INTEGER);
                        ps.setInt(6, editIdUsuario);
                        int n = ps.executeUpdate();
                        if (n == 1) {
                            if (onSaved != null) onSaved.run();
                            cancelar();
                        } else alerta(Alert.AlertType.ERROR, "No se pudo actualizar el usuario.");
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(sqlConPass)) {
                        ps.setString(1, nombre);
                        ps.setString(2, apellidos);
                        ps.setString(3, correo);
                        ps.setInt(4, idRol);
                        ps.setString(5, pass);
                        if (idDivision != null) ps.setInt(6, idDivision); else ps.setNull(6, Types.INTEGER);
                        ps.setInt(7, editIdUsuario);
                        int n = ps.executeUpdate();
                        if (n == 1) {
                            if (onSaved != null) onSaved.run();
                            cancelar();
                        } else alerta(Alert.AlertType.ERROR, "No se pudo actualizar el usuario.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error SQL: " + e.getMessage());
        }
    }

    private void cargarRoles() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ID_ROL, NOMBRE_ROL FROM ROL ORDER BY ID_ROL");
             ResultSet rs = ps.executeQuery()) {
            rolMap.clear();
            while (rs.next()) {
                rolMap.put(rs.getString("NOMBRE_ROL"), rs.getInt("ID_ROL"));
            }
            cmbRol.setItems(javafx.collections.FXCollections.observableArrayList(rolMap.keySet()));
        } catch (SQLException e) {
            e.printStackTrace(); // sin pop-ups
        }
    }

    private void alerta(Alert.AlertType t, String msg) {
        new Alert(t, msg, ButtonType.OK).showAndWait();
    }
}
