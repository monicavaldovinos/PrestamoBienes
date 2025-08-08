module com.rebup {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports com.rebup;
    exports com.rebup.db;

    opens com.rebup to javafx.fxml;
    
}
