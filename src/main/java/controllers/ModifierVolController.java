package controllers;


import entities.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CrudVol;
import javafx.collections.FXCollections;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import services.MailService;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

public class ModifierVolController {

    @FXML
    private ComboBox<StatutVol> statutComboBox;
    @FXML private ComboBox<String> enPromotionComboBox;
    @FXML private TextField pourcentageField;

    @FXML private ComboBox<String> departField;
    @FXML private ComboBox<String> destinationField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField prixField;
    @FXML
    private Label pourcentageLabel;
    @FXML
    private ComboBox<Enumnom> categorieComboBox;
    @FXML
    public void initialize() {
        statutComboBox.setItems(FXCollections.observableArrayList(StatutVol.values()));


        enPromotionComboBox.setOnAction(event -> {
            String selected = enPromotionComboBox.getValue();
            boolean showPourcentage = "enpromotion".equalsIgnoreCase(selected);
            pourcentageField.setVisible(showPourcentage);
            pourcentageField.setManaged(showPourcentage);
            pourcentageLabel.setVisible(showPourcentage);
            pourcentageLabel.setManaged(showPourcentage);
        });

        pourcentageField.setVisible(false);
        pourcentageLabel.setVisible(false);
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
        try {
            // Récupérer l'ancien état avant modification
            boolean etaitEnPromotionAvant = "enpromotion".equalsIgnoreCase(vol.getEnpromotion());

            System.out.println("Bouton Enregistrer cliqué");

            // Valider les champs obligatoires
            if (departField.getValue() == null || destinationField.getValue() == null
                    || datePicker.getValue() == null || prixField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Mettre à jour les propriétés du vol
            vol.setDepart(departField.getValue());
            vol.setDestination(destinationField.getValue());
            vol.setStatut(statutComboBox.getValue());
            vol.setDate(java.sql.Date.valueOf(datePicker.getValue()));

            if (dateRetourPicker.getValue() != null) {
                vol.setDateRetour(java.sql.Date.valueOf(dateRetourPicker.getValue()));
            } else {
                vol.setDateRetour(null);
            }

            try {
                vol.setPrix(Double.parseDouble(prixField.getText()));
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Prix invalide");
                return;
            }

            // Gestion de la catégorie
            Enumnom selectedNom = categorieComboBox.getValue();
            if (selectedNom == null) {
                showAlert("Erreur", "Veuillez sélectionner une catégorie");
                return;
            }

            try {
                CategorieVol categorie = crudVol.getCategorieByNom(selectedNom.name());
                if (categorie != null) {
                    vol.setCategorie(categorie);
                } else {
                    System.out.println("Catégorie non trouvée !");
                    showAlert("Erreur", "Catégorie non trouvée");
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Problème d'accès à la base de données");
                return;
            }

            // Gestion de la promotion
            String enPromo = enPromotionComboBox.getValue();
            vol.setEnpromotion(enPromo);
            enPromotionComboBox.setValue(vol.getEnpromotion());
            if ("enpromotion".equalsIgnoreCase(enPromo)) {
                String pourcentageText = pourcentageField.getText();
                if (pourcentageText != null && !pourcentageText.trim().isEmpty()) {
                    try {
                        double pourcentage = Double.parseDouble(pourcentageText.trim());
                        if (pourcentage <= 0 || pourcentage >= 100) {
                            showAlert("Erreur", "Le pourcentage doit être entre 0 et 100");
                            return;
                        }
                        vol.setPourcentagePromotion(pourcentage);
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Pourcentage invalide");
                        return;
                    }
                } else {
                    showAlert("Erreur", "Veuillez saisir un pourcentage de promotion");
                    return;
                }
            } else {
                vol.setPourcentagePromotion(0);
            }

            // Enregistrer les modifications
            crudVol.modifierVol(vol);

            // Vérifier si la promotion vient d'être activée
            boolean estEnPromotionMaintenant = "enpromotion".equalsIgnoreCase(vol.getEnpromotion());
            boolean promotionJusteActivee = estEnPromotionMaintenant && !etaitEnPromotionAvant;

            // Envoyer les emails si promotion vient d'être activée
            if (promotionJusteActivee) {
                new Thread(() -> {
                    try {
                        MailService mailService = new MailService();
                        mailService.envoyerPromotionAuxClients(vol);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'envoi des emails");
                        e.printStackTrace();
                    }
                }).start();
            }

            // Fermer la fenêtre
            ((Stage) departField.getScene().getWindow()).close();

            showAlert("Succès", "Vol modifié avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}