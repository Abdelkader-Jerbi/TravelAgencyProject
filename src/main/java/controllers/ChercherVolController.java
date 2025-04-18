package controllers;
import entities.Enumnom;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChercherVolController implements Initializable {
    @FXML
    private ChoiceBox<Enumnom> categorieChoiceBox;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categorieChoiceBox.getItems().addAll(Enumnom.values());
    }
}
