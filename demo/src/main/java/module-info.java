module com.rebup {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.rebup;
    exports com.rebup.config;
    exports com.rebup.ui.controller;

    opens com.rebup to javafx.fxml;
    opens com.rebup.config to javafx.fxml;
    opens com.rebup.ui.controller to javafx.fxml;
    opens com.rebup.model to javafx.base, javafx.fxml;
    exports com.rebup.model;
}