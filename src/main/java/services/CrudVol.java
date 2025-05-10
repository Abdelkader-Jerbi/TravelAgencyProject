package services;

import entities.Vol;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Random;

/**
 * Service CRUD pour la gestion des vols
 */
public class CrudVol implements ICrudService<Vol> {
    
    private Connection connection;
    
    /**
     * Constructeur qui initialise la connexion à la base de données
     */
    public CrudVol() {
        connection = MyDatabase.getInstance().getConnection();
    }
    
    /**
     * Ajoute un vol à la base de données
     * 
     * @param vol Le vol à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    @Override
    public boolean ajouter(Vol vol) {
        String sql = "INSERT INTO vol (numVol, compagnie, depart, destination, dateDepart, heure, prix, capacite, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, vol.getNumVol());
            pst.setString(2, vol.getCompagnie());
            pst.setString(3, vol.getDepart());
            pst.setString(4, vol.getDestination());
            pst.setString(5, vol.getDateDepart());
            pst.setString(6, vol.getHeure());
            pst.setDouble(7, vol.getPrix());
            pst.setInt(8, vol.getCapacite());
            pst.setString(9, vol.getStatut());
            
            System.out.println("Exécution de la requête SQL: " + sql);
            System.out.println("Paramètres: " + vol.getNumVol() + ", " + vol.getCompagnie() + ", " + vol.getDepart() + 
                              ", " + vol.getDestination() + ", " + vol.getDateDepart() + ", " + vol.getHeure() + 
                              ", " + vol.getPrix() + ", " + vol.getCapacite() + ", " + vol.getStatut());
            
            int rowsAffected = pst.executeUpdate();
            System.out.println("Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du vol : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Modifie un vol dans la base de données
     * 
     * @param vol Le vol à modifier
     * @return true si la modification a réussi, false sinon
     */
    @Override
    public boolean modifier(Vol vol) {
        String sql = "UPDATE vol SET numVol = ?, compagnie = ?, depart = ?, destination = ?, " +
                     "dateDepart = ?, heure = ?, prix = ?, capacite = ?, statut = ? " +
                     "WHERE idVol = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, vol.getNumVol());
            pst.setString(2, vol.getCompagnie());
            pst.setString(3, vol.getDepart());
            pst.setString(4, vol.getDestination());
            pst.setString(5, vol.getDateDepart());
            pst.setString(6, vol.getHeure());
            pst.setDouble(7, vol.getPrix());
            pst.setInt(8, vol.getCapacite());
            pst.setString(9, vol.getStatut());
            pst.setInt(10, vol.getIdVol());
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du vol : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un vol de la base de données
     * 
     * @param id L'ID du vol à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM vol WHERE idVol = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du vol : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un vol par son ID
     * 
     * @param id L'ID du vol à récupérer
     * @return Le vol correspondant à l'ID, ou null si aucun vol n'est trouvé
     */
    @Override
    public Vol getById(int id) {
        String sql = "SELECT * FROM vol WHERE idVol = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVol(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du vol : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les vols de la base de données
     * 
     * @return Une liste de tous les vols
     */
    @Override
    public List<Vol> getAll() {
        List<Vol> vols = new ArrayList<>();
        String sql = "SELECT * FROM vol";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                vols.add(mapResultSetToVol(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des vols : " + e.getMessage());
        }
        
        return vols;
    }
    
    /**
     * Recherche des vols selon des critères spécifiques
     * 
     * @param depart La ville de départ
     * @param destination La ville de destination
     * @param dateDepart La date de départ
     * @return Une liste des vols correspondant aux critères
     */
    public List<Vol> rechercherVols(String depart, String destination, String dateDepart) {
        List<Vol> vols = new ArrayList<>();
        String sql = "SELECT * FROM vol WHERE depart = ? AND destination = ? AND dateDepart = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, depart);
            pst.setString(2, destination);
            pst.setString(3, dateDepart);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    vols.add(mapResultSetToVol(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des vols : " + e.getMessage());
        }
        
        return vols;
    }
    
    /**
     * Vérifie si la table vol existe dans la base de données et la crée si nécessaire
     */
    public void ensureTableExists() {
        String checkTableSQL = "SHOW TABLES LIKE 'vol'";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(checkTableSQL)) {
            
            if (!rs.next()) {
                // La table n'existe pas, on la crée
                String createTableSQL = "CREATE TABLE vol (" +
                        "idVol INT PRIMARY KEY AUTO_INCREMENT, " +
                        "numVol VARCHAR(20) NOT NULL, " +
                        "compagnie VARCHAR(100) NOT NULL, " +
                        "depart VARCHAR(100) NOT NULL, " +
                        "destination VARCHAR(100) NOT NULL, " +
                        "dateDepart VARCHAR(20) NOT NULL, " +
                        "heure VARCHAR(10) NOT NULL, " +
                        "prix DOUBLE NOT NULL, " +
                        "capacite INT NOT NULL, " +
                        "statut VARCHAR(50) NOT NULL" +
                        ")";
                
                st.executeUpdate(createTableSQL);
                System.out.println("Table 'vol' créée avec succès.");
                
                // Ajout de quelques vols de démonstration
                ajouterVolsDemonstration();
            } else {
                // La table existe, mais vérifions si elle contient des données
                String countSQL = "SELECT COUNT(*) FROM vol";
                try (ResultSet countRs = st.executeQuery(countSQL)) {
                    if (countRs.next() && countRs.getInt(1) < 10) {
                        // Moins de 10 vols, ajoutons des données de démonstration
                        ajouterVolsDemonstration();
                    } else {
                        System.out.println("Table 'vol' existe déjà avec " + countRs.getInt(1) + " enregistrements.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table vol : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ajoute des vols de démonstration à la base de données
     */
    private void ajouterVolsDemonstration() {
        System.out.println("Ajout de vols de démonstration...");
        
        // Liste des villes populaires
        String[] villes = {
            "Paris", "Londres", "New York", "Rome", "Madrid", "Berlin", 
            "Amsterdam", "Barcelone", "Lisbonne", "Dubaï", "Istanbul", "Tokyo",
            "Sydney", "Los Angeles", "Miami", "Montréal", "Rio de Janeiro",
            "Le Caire", "Marrakech", "Tunis", "Alger", "Casablanca"
        };
        
        // Liste des compagnies aériennes
        String[] compagnies = {
            "Air France", "British Airways", "Lufthansa", "Emirates", 
            "Qatar Airways", "Turkish Airlines", "Iberia", "KLM", 
            "Alitalia", "American Airlines", "Delta", "United Airlines",
            "Tunisair", "Royal Air Maroc", "Air Algérie", "EgyptAir"
        };
        
        // Dates de départ (prochains 30 jours)
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        // Heures de départ
        String[] heures = {
            "06:00", "07:30", "09:00", "10:30", "12:00", "13:30", 
            "15:00", "16:30", "18:00", "19:30", "21:00", "22:30"
        };
        
        // Générer des vols entre différentes villes
        Random random = new Random();
        
        try {
            // Supprimer les vols existants
            String deleteSQL = "DELETE FROM vol";
            try (Statement st = connection.createStatement()) {
                st.executeUpdate(deleteSQL);
            }
            
            // Limiter le nombre de villes pour éviter de générer trop de vols
            int maxVilles = Math.min(villes.length, 10);
            
            System.out.println("Génération de vols pour " + maxVilles + " villes...");
            
            // Ajouter de nouveaux vols
            for (int i = 0; i < maxVilles; i++) {
                for (int j = 0; j < maxVilles; j++) {
                    if (i != j) { // Éviter les vols d'une ville à elle-même
                        String villeDepart = villes[i];
                        String villeDestination = villes[j];
                        
                        // Ajouter 1-2 vols pour chaque paire de villes
                        int nbVols = 1 + random.nextInt(2); // 1 ou 2 vols
                        
                        System.out.println("Ajout de " + nbVols + " vols de " + villeDepart + " à " + villeDestination);
                        
                        for (int k = 0; k < nbVols; k++) {
                            // Choisir une date aléatoire parmi les 30 prochains jours
                            String dateDepart = dates.get(random.nextInt(dates.size()));
                            
                            // Choisir une compagnie aléatoire
                            String compagnie = compagnies[random.nextInt(compagnies.length)];
                            
                            // Générer un numéro de vol
                            String prefixe = compagnie.substring(0, 2).toUpperCase();
                            String numVol = prefixe + (1000 + random.nextInt(9000));
                            
                            // Choisir une heure aléatoire
                            String heure = heures[random.nextInt(heures.length)];
                            
                            // Générer un prix aléatoire entre 100 et 1000
                            double prix = 100 + random.nextDouble() * 900;
                            prix = Math.round(prix * 100) / 100.0; // Arrondir à 2 décimales
                            
                            // Capacité aléatoire entre 150 et 300
                            int capacite = 150 + random.nextInt(151);
                            
                            try {
                                // Créer et ajouter le vol
                                Vol vol = new Vol(numVol, compagnie, villeDepart, villeDestination, 
                                                 dateDepart, heure, prix, capacite, "Disponible");
                                boolean success = ajouter(vol);
                                if (success) {
                                    System.out.println("Vol ajouté: " + numVol + " - " + compagnie + " - " + villeDepart + " à " + villeDestination);
                                } else {
                                    System.err.println("Échec de l'ajout du vol: " + numVol);
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur lors de l'ajout du vol: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            
            System.out.println("Vols de démonstration ajoutés avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des vols de démonstration : " + e.getMessage());
        }
    }

    
    /**
     * Convertit un ResultSet en objet Vol
     * 
     * @param rs Le ResultSet à convertir
     * @return Un objet Vol
     * @throws SQLException Si une erreur SQL survient
     */
    private Vol mapResultSetToVol(ResultSet rs) throws SQLException {
        return new Vol(
                rs.getInt("idVol"),
                rs.getString("numVol"),
                rs.getString("compagnie"),
                rs.getString("depart"),
                rs.getString("destination"),
                rs.getString("dateDepart"),
                rs.getString("heure"),
                rs.getDouble("prix"),
                rs.getInt("capacite"),
                rs.getString("statut")
        );
    }
}