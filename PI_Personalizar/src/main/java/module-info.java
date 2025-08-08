module utez.com.mx.pi_personalizar {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens utez.com.mx.pi_personalizar to javafx.fxml;
    exports utez.com.mx.pi_personalizar;
}