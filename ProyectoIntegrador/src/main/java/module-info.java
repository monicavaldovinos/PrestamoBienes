module utez.edu.mx.proyectointegrador {
    requires javafx.controls;
    requires javafx.fxml;

    opens utez.edu.mx.proyectointegrador.controller to javafx.fxml;
    opens utez.edu.mx.proyectointegrador.model to javafx.base;
    exports utez.edu.mx.proyectointegrador;
}