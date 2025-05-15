package controllers;

import entities.Categorie;
import entities.Enumnom;
import entities.StatutVol;
import entities.Vol;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CrudVol;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

public class ModifierVolController {
    @FXML
    private ComboBox<StatutVol> statutComboBox;

    @FXML private ComboBox<String> departField;
    @FXML private ComboBox<String> destinationField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField prixField;
    @FXML
    private ComboBox<Enumnom> categorieComboBox;
    @FXML
    public void initialize() {
        statutComboBox.setItems(FXCollections.observableArrayList(StatutVol.values()));

        categorieComboBox.setItems(FXCollections.observableArrayList(Enumnom.values()));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://restcountries.com/v3.1/all"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        JSONArray array = new JSONArray(json);
                        List<String> pays = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject country = array.getJSONObject(i);
                            String name = country.getJSONObject("name").getString("common");
                            pays.add(name);
                        }
                        Platform.runLater(() -> {
                            departField.getItems().addAll(pays);
                            destinationField.getItems().addAll(pays);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

    }

    private Vol vol;
    private final CrudVol crudVol = new CrudVol();


    public void setVol(Vol vol) {
        this.vol = vol;
        departField.setValue(vol.getDepart());              // ← ComboBox
        destinationField.setValue(vol.getDestination());
        statutComboBox.setValue(vol.getStatut());
        datePicker.setValue(new java.sql.Date(vol.getDate().getTime()).toLocalDate());
        if (vol.getDateRetour() != null)
            dateRetourPicker.setValue(new java.sql.Date(vol.getDateRetour().getTime()).toLocalDate());
        prixField.setText(String.valueOf(vol.getPrix()));
        categorieComboBox.setValue(vol.getCategorie().getNom());

    }

    @FXML
    private void modifierVol() {
        System.out.println("Bouton Enregistrer cliqué");
        vol.setDepart(departField.getValue());
        vol.setDestination(destinationField.getValue());
        vol.setStatut(statutComboBox.getValue());
        vol.setDate(java.sql.Date.valueOf(datePicker.getValue()));
        if (dateRetourPicker.getValue() != null) {
            vol.setDateRetour(java.sql.Date.valueOf(dateRetourPicker.getValue()));
        }

        vol.setPrix(Double.parseDouble(prixField.getText()));
        Enumnom selectedNom = categorieComboBox.getValue();
        Categorie categorie = null;
        try {
            categorie = crudVol.getCategorieByNom(selectedNom.name());
        } catch (SQLException e) {
            e.printStackTrace();

        }

        if (categorie != null) {
            vol.setCategorie(categorie);
        } else {
            System.out.println("Catégorie non trouvée !");

        }
        crudVol.modifierVol(vol);
        ((Stage) departField.getScene().getWindow()).close();

    }
}
