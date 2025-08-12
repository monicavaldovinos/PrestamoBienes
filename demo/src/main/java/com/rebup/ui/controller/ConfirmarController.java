package com.rebup.ui.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.rebup.config.DBConnection;
import com.rebup.dao.ObjetoDao;
import com.rebup.dao.ObjetoDaoImpl;
import com.rebup.model.Objeto;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConfirmarController {

    private List<Objeto> objetosSeleccionados;

    public void setObjetosSeleccionados(List<Objeto> objetos) {
        this.objetosSeleccionados = objetos;
    }

    @FXML
    private void cerrarVentana() {
        try (Connection conn = DBConnection.getConnection()) {
            ObjetoDao dao = new ObjetoDaoImpl(conn);

            for (Objeto obj : objetosSeleccionados) {
            
                dao.actualizarCantidadYEstado(obj.getIdObjeto(), obj.getCantidad());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
        stage.close();
    }
}
