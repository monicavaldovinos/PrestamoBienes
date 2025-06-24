module historialprestamos.java.historiales {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens historialprestamos.java.historiales to javafx.fxml;
    exports historialprestamos.java.historiales;
}