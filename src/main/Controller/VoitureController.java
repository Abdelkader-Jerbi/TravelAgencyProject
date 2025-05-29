@FXML
public void initialize() {
    // (éventuellement, charger les filtres, etc.)
    if (conn == null) {
        // (log ou afficher une erreur, par exemple via un Alert ou un log)
        System.err.println("Erreur : connexion (conn) est nulle. Impossible de charger les voitures.");
        return;
    }
    loadAllVoitures();
} 