module com.rebup {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.rebup;

    opens com.rebup to javafx.fxml;
    
}
