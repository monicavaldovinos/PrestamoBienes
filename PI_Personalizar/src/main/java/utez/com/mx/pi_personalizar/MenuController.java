package utez.com.mx.pi_personalizar;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.Scene;

public class MenuController {

    @FXML
    private ComboBox<String> themeSelector;

    @FXML
    public void initialize() {
        themeSelector.getItems().addAll("Azul", "Verde", "Gris");

        themeSelector.setOnAction(e -> {
            String selected = themeSelector.getValue();
            String cssFile = switch (selected) {
                case "Azul" -> "/utez/com/mx/pi_personalizar/CSS/Theme_blue.css";
                case "Verde" -> "/utez/com/mx/pi_personalizar/CSS/theme_green.css";
                case "Gris" -> "/utez/com/mx/pi_personalizar/CSS/theme_dark.css";
                default -> null;
            };

            if (cssFile != null) {
                Scene scene = themeSelector.getScene();
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
            }
        });
    }
}

