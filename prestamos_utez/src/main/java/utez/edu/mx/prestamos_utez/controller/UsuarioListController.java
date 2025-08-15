package utez.edu.mx.prestamos_utez.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utez.edu.mx.prestamos_utez.config.DBConnection;
import utez.edu.mx.prestamos_utez.model.Usuario;

import java.io.IOException;
import java.sql.*;

public class UsuarioListController {

    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, Number> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellidos;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colDivision;
    @FXML private TableColumn<Usuario, Void> colAcciones;

    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(tabla.getItems().indexOf(c.getValue()) + 1));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colApellidos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellidos()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreRol()));
        colDivision.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNombreDivision() != null ? c.getValue().getNombreDivision() : ""
        ));
        configurarAcciones();
        cargar();
    }

    /** Carga los usuarios desde BD (con rol y división) */
    private void cargar() {
        String sql = """
            SELECT u.ID_USUARIO, u.NOMBRE, u.APELLIDOS, u.CORREO, u.ID_ROL,
                   r.NOMBRE_ROL, d.NOMBRE AS NOMBRE_DIVISION
              FROM USUARIO u
              LEFT JOIN ROL r ON r.ID_ROL = u.ID_ROL
              LEFT JOIN DIVISION d ON d.ID_DIVISION = u.ID_DIVISION
             ORDER BY u.ID_USUARIO ASC
        """;
        var lista = FXCollections.<Usuario>observableArrayList();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("ID_USUARIO"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setApellidos(rs.getString("APELLIDOS"));
                u.setCorreo(rs.getString("CORREO"));
                u.setIdRol(rs.getInt("ID_ROL"));
                u.setNombreRol(rs.getString("NOMBRE_ROL"));
                u.setNombreDivision(rs.getString("NOMBRE_DIVISION"));
                lista.add(u);
            }
            tabla.setItems(lista);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error SQL al listar usuarios: " + e.getMessage()).showAndWait();
        }
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            final Button btnEditar = new Button();
            final Button btnEliminar = new Button();
            final HBox box = new HBox(6, btnEditar, btnEliminar);

            {
                // Iconos (asegúrate de que existan en /resources/utez/edu/mx/prestamos_utez/icons/)
                ImageView iconEdit = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/editar.png").toExternalForm()
                );
                iconEdit.setFitWidth(17); iconEdit.setFitHeight(17);
                btnEditar.setGraphic(iconEdit);
                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEditar.setOnAction(e -> {
                    Usuario u = getItemRow();
                    if (u != null) abrirFormEdicion(u);
                });

                ImageView iconTrash = new ImageView(
                        getClass().getResource("/utez/edu/mx/prestamos_utez/icons/eliminar.png").toExternalForm()
                );
                iconTrash.setFitWidth(17); iconTrash.setFitHeight(17);
                btnEliminar.setGraphic(iconTrash);
                btnEliminar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setOnAction(e -> {
                    Usuario u = getItemRow();
                    if (u != null) eliminarUsuario(u.getIdUsuario());
                });
            }

            private Usuario getItemRow() {
                int i = getIndex();
                return (i >= 0 && i < getTableView().getItems().size())
                        ? getTableView().getItems().get(i) : null;
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM USUARIO WHERE ID_USUARIO = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            int n = ps.executeUpdate();
            if (n == 1) {
                cargar(); // refresca sin pop-up
            } else {
                new Alert(Alert.AlertType.ERROR, "No se pudo eliminar.").showAndWait();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "No se puede eliminar: el usuario tiene registros relacionados.").showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error SQL: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void nuevo() {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/usuario_form.fxml"));
            Parent root = fx.load();
            UsuarioFormController c = fx.getController();
            c.setUsuario(null, null, null, null, null, null, this::cargar);

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Nuevo usuario");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario").show();
        }
    }

    private void abrirFormEdicion(Usuario u) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(
                    "/utez/edu/mx/prestamos_utez/view/usuario_form.fxml"));
            Parent root = fx.load();
            UsuarioFormController c = fx.getController();
            c.setUsuario(
                    u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo(),
                    u.getIdRol(), u.getNombreRol(), this::cargar
            );

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Editar usuario");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario").showAndWait();
        }
    }
}
