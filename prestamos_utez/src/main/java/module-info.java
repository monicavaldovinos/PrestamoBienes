module utez.edu.mx.prestamos_utez {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens utez.edu.mx.prestamos_utez to javafx.fxml;
    opens utez.edu.mx.prestamos_utez.controller to javafx.fxml;
    opens utez.edu.mx.prestamos_utez.model to javafx.base, javafx.fxml;
    exports utez.edu.mx.prestamos_utez;
    exports utez.edu.mx.prestamos_utez.ui.controller;
    opens utez.edu.mx.prestamos_utez.ui.controller to javafx.fxml;
}