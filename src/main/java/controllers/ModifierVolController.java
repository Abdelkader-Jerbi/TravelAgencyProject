package controllers;

import entities.Categorie;
import entities.Enumnom;
import entities.Vol;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CrudVol;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;

public class ModifierVolController {
    @FXML private TextField departField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField prixField;
    @FXML
    private ComboBox<Enumnom> categorieComboBox;
    @FXML
    public void initialize() {
        categorieComboBox.setItems(FXCollections.observableArrayList(Enumnom.values()));
    }

    private Vol vol;
    private final CrudVol crudVol = new CrudVol();


    public void setVol(Vol vol) {
        this.vol = vol;
        departField.setText(vol.getDepart());
        destinationField.setText(vol.getDestination());
        datePicker.setValue(new java.sql.Date(vol.getDate().getTime()).toLocalDate());
        if (vol.getDateRetour() != null)
            dateRetourPicker.setValue(new java.sql.Date(vol.getDateRetour().getTime()).toLocalDate());
        prixField.setText(String.valueOf(vol.getPrix()));
        categorieComboBox.setValue(vol.getCategorie().getNom());

    }

    @FXML
    private void modifierVol() {
        System.out.println("Bouton Enregistrer cliqué");
        vol.setDepart(departField.getText());
        vol.setDestination(destinationField.getText());
        vol.setDate(java.sql.Date.valueOf(datePicker.getValue()));
        if (dateRetourPicker.getValue() != null) {
            vol.setDateRetour(java.sql.Date.valueOf(dateRetourPicker.getValue()));
        }

        vol.setPrix(Double.parseDouble(prixField.getText()));
        // Récupérer la catégorie en fonction du nom sélectionné dans le ComboBox
        Enumnom selectedNom = categorieComboBox.getValue();
        Categorie categorie = null;
        // À adapter selon ta classe `Categorie`
        try {
            categorie = crudVol.getCategorieByNom(selectedNom.name()); // Récupérer la catégorie par son nom
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception, par exemple, afficher un message d'erreur ou utiliser une catégorie par défaut
        }

        if (categorie != null) {
            vol.setCategorie(categorie); // Mettre à jour la catégorie du vol avec l'objet récupéré
        } else {
            System.out.println("Catégorie non trouvée !");
            // Traiter le cas où la catégorie n'est pas trouvée (par exemple, afficher un message d'erreur)
        }
        crudVol.modifierVol(vol);
        ((Stage) departField.getScene().getWindow()).close();

    }
}
