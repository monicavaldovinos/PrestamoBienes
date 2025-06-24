module utez.com.mx.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens utez.com.mx.demo to javafx.fxml;
    exports utez.com.mx.demo;
}