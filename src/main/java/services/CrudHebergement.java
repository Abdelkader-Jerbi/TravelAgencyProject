package services;

import entities.Hebergement;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service CRUD pour la gestion des hébergements
 */
public class CrudHebergement implements ICrudService<Hebergement> {
    
    private Connection connection;
    
    /**
     * Constructeur qui initialise la connexion à la base de données
     */
    public CrudHebergement() {
        connection = MyDatabase.getInstance().getConnection();
    }
    
    /**
     * Ajoute un hébergement à la base de données
     * 
     * @param hebergement L'hébergement à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    @Override
    public boolean ajouter(Hebergement hebergement) {
        String sql = "INSERT INTO hebergement (nom, type, adresse, ville, etoiles, prixNuit, capacite, description, disponibilite) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, hebergement.getNom());
            pst.setString(2, hebergement.getType());
            pst.setString(3, hebergement.getAdresse());
            pst.setString(4, hebergement.getVille());
            pst.setInt(5, hebergement.getEtoiles());
            pst.setDouble(6, hebergement.getPrixNuit());
            pst.setInt(7, hebergement.getCapacite());
            pst.setString(8, hebergement.getDescription());
            pst.setString(9, hebergement.getDisponibilite());
            
            System.out.println("Exécution de la requête SQL: " + sql);
            System.out.println("Paramètres: " + hebergement.getNom() + ", " + hebergement.getType() + ", " + hebergement.getAdresse() + 
                              ", " + hebergement.getVille() + ", " + hebergement.getEtoiles() + ", " + hebergement.getPrixNuit() + 
                              ", " + hebergement.getCapacite() + ", " + hebergement.getDescription() + ", " + hebergement.getDisponibilite());
            
            int rowsAffected = pst.executeUpdate();
            System.out.println("Lignes affectées: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'hébergement : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Modifie un hébergement dans la base de données
     * 
     * @param hebergement L'hébergement à modifier
     * @return true si la modification a réussi, false sinon
     */
    @Override
    public boolean modifier(Hebergement hebergement) {
        String sql = "UPDATE hebergement SET nom = ?, type = ?, adresse = ?, ville = ?, " +
                     "etoiles = ?, prixNuit = ?, capacite = ?, description = ?, disponibilite = ? " +
                     "WHERE idHebergement = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, hebergement.getNom());
            pst.setString(2, hebergement.getType());
            pst.setString(3, hebergement.getAdresse());
            pst.setString(4, hebergement.getVille());
            pst.setInt(5, hebergement.getEtoiles());
            pst.setDouble(6, hebergement.getPrixNuit());
            pst.setInt(7, hebergement.getCapacite());
            pst.setString(8, hebergement.getDescription());
            pst.setString(9, hebergement.getDisponibilite());
            pst.setInt(10, hebergement.getIdHebergement());
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'hébergement : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un hébergement de la base de données
     * 
     * @param id L'ID de l'hébergement à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM hebergement WHERE idHebergement = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'hébergement : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un hébergement par son ID
     * 
     * @param id L'ID de l'hébergement à récupérer
     * @return L'hébergement correspondant à l'ID, ou null si aucun hébergement n'est trouvé
     */
    @Override
    public Hebergement getById(int id) {
        String sql = "SELECT * FROM hebergement WHERE idHebergement = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHebergement(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'hébergement : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les hébergements de la base de données
     * 
     * @return Une liste de tous les hébergements
     */
    @Override
    public List<Hebergement> getAll() {
        List<Hebergement> hebergements = new ArrayList<>();
        String sql = "SELECT * FROM hebergement";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                hebergements.add(mapResultSetToHebergement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hébergements : " + e.getMessage());
        }
        
        return hebergements;
    }
    
    /**
     * Recherche des hébergements selon des critères spécifiques
     * 
     * @param ville La ville de l'hébergement
     * @param type Le type d'hébergement
     * @return Une liste des hébergements correspondant aux critères
     */
    public List<Hebergement> rechercherHebergements(String ville, String type) {
        List<Hebergement> hebergements = new ArrayList<>();
        String sql = "SELECT * FROM hebergement WHERE ville = ? AND type = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ville);
            pst.setString(2, type);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    hebergements.add(mapResultSetToHebergement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des hébergements : " + e.getMessage());
        }
        
        return hebergements;
    }
    
    /**
     * Recherche des hébergements disponibles dans une ville donnée
     * 
     * @param ville La ville de l'hébergement
     * @return Une liste des hébergements disponibles dans la ville
     */
    public List<Hebergement> rechercherHebergementParVille(String ville) {
        List<Hebergement> hebergements = new ArrayList<>();
        String sql = "SELECT * FROM hebergement WHERE ville = ? AND disponibilite = 'Disponible'";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ville);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    hebergements.add(mapResultSetToHebergement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des hébergements par ville : " + e.getMessage());
        }
        
        return hebergements;
    }
    
    /**
     * Vérifie si la table hebergement existe dans la base de données et la crée si nécessaire
     */
    public void ensureTableExists() {
        String checkTableSQL = "SHOW TABLES LIKE 'hebergement'";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(checkTableSQL)) {
            
            if (!rs.next()) {
                // La table n'existe pas, on la crée
                String createTableSQL = "CREATE TABLE hebergement (" +
                        "idHebergement INT PRIMARY KEY AUTO_INCREMENT, " +
                        "nom VARCHAR(100) NOT NULL, " +
                        "type VARCHAR(50) NOT NULL, " +
                        "adresse VARCHAR(200) NOT NULL, " +
                        "ville VARCHAR(100) NOT NULL, " +
                        "etoiles INT NOT NULL, " +
                        "prixNuit DOUBLE NOT NULL, " +
                        "capacite INT NOT NULL, " +
                        "description TEXT, " +
                        "disponibilite VARCHAR(50) NOT NULL" +
                        ")";
                
                st.executeUpdate(createTableSQL);
                System.out.println("Table 'hebergement' créée avec succès.");
                
                // Ajout de quelques hébergements de démonstration
                ajouterHebergementsDemonstration();
            } else {
                // La table existe, mais vérifions si elle contient des données
                String countSQL = "SELECT COUNT(*) FROM hebergement";
                try (ResultSet countRs = st.executeQuery(countSQL)) {
                    if (countRs.next() && countRs.getInt(1) < 10) {
                        // Moins de 10 hébergements, ajoutons des données de démonstration
                        ajouterHebergementsDemonstration();
                    } else {
                        System.out.println("Table 'hebergement' existe déjà avec " + countRs.getInt(1) + " enregistrements.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table hebergement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ajoute des hébergements de démonstration à la base de données
     */
    private void ajouterHebergementsDemonstration() {
        System.out.println("Ajout d'hébergements de démonstration...");
        
        // Liste des villes populaires
        String[] villes = {
            "Paris", "Londres", "New York", "Rome", "Madrid", "Berlin", 
            "Amsterdam", "Barcelone", "Lisbonne", "Dubaï", "Istanbul", "Tokyo",
            "Sydney", "Los Angeles", "Miami", "Montréal", "Rio de Janeiro",
            "Le Caire", "Marrakech", "Tunis", "Alger", "Casablanca"
        };
        
        // Types d'hébergement
        String[] types = {
            "Hôtel", "Appartement", "Maison", "Villa", "Résidence", "Gîte"
        };
        
        // Noms d'hôtels par type
        Map<String, List<String>> nomsPrefixes = new HashMap<>();
        nomsPrefixes.put("Hôtel", Arrays.asList("Grand Hôtel", "Hôtel Royal", "Hôtel Luxe", "Hôtel Central", "Hôtel Palace"));
        nomsPrefixes.put("Appartement", Arrays.asList("Appartement Vue", "Appartement Central", "Appartement Luxe", "Studio", "Loft"));
        nomsPrefixes.put("Maison", Arrays.asList("Maison Traditionnelle", "Maison de Ville", "Maison Moderne", "Cottage", "Maison de Charme"));
        nomsPrefixes.put("Villa", Arrays.asList("Villa Luxueuse", "Villa avec Piscine", "Villa Panoramique", "Villa Élégante", "Villa Royale"));
        nomsPrefixes.put("Résidence", Arrays.asList("Résidence du Parc", "Résidence Centrale", "Résidence des Jardins", "Résidence Élégante", "Résidence Moderne"));
        nomsPrefixes.put("Gîte", Arrays.asList("Gîte Rural", "Gîte de Charme", "Gîte Traditionnel", "Gîte Familial", "Gîte Rustique"));
        
        // Adresses génériques
        String[] rues = {
            "Avenue Principale", "Rue du Centre", "Boulevard Central", 
            "Place de la République", "Rue de la Paix", "Avenue des Champs",
            "Rue du Marché", "Boulevard des Arts", "Avenue de la Mer",
            "Rue de la Montagne", "Place du Village"
        };
        
        // Descriptions par type
        Map<String, List<String>> descriptions = new HashMap<>();
        descriptions.put("Hôtel", Arrays.asList(
            "Un hôtel élégant offrant un service impeccable et des chambres luxueuses.",
            "Cet hôtel de charme combine confort moderne et architecture traditionnelle.",
            "Un établissement prestigieux avec restaurant gastronomique et spa.",
            "Hôtel de luxe avec vue panoramique et service personnalisé."
        ));
        descriptions.put("Appartement", Arrays.asList(
            "Appartement spacieux et lumineux, idéalement situé au cœur de la ville.",
            "Studio moderne avec toutes les commodités nécessaires pour un séjour agréable.",
            "Loft design avec terrasse offrant une vue imprenable sur la ville.",
            "Appartement de charme dans un quartier historique et animé."
        ));
        descriptions.put("Maison", Arrays.asList(
            "Maison traditionnelle avec jardin, parfaite pour les familles.",
            "Maison de ville rénovée avec goût, à proximité des attractions principales.",
            "Charmante maison avec terrasse et barbecue pour profiter des soirées d'été.",
            "Maison spacieuse et confortable dans un quartier calme et résidentiel."
        ));
        descriptions.put("Villa", Arrays.asList(
            "Villa luxueuse avec piscine privée et jardin tropical.",
            "Magnifique villa avec vue sur mer et accès direct à la plage.",
            "Villa élégante avec intérieur design et extérieur paisible.",
            "Villa exclusive avec service de conciergerie et sécurité 24h/24."
        ));
        descriptions.put("Résidence", Arrays.asList(
            "Résidence moderne avec piscine commune et espace de détente.",
            "Appartement en résidence sécurisée avec parking et jardin.",
            "Résidence de standing avec réception et service de ménage.",
            "Logement confortable dans une résidence calme et bien entretenue."
        ));
        descriptions.put("Gîte", Arrays.asList(
            "Gîte rural authentique au cœur de la nature.",
            "Gîte de charme dans un ancien corps de ferme rénové.",
            "Hébergement traditionnel avec cheminée et décoration rustique.",
            "Gîte paisible entouré de verdure, idéal pour se ressourcer."
        ));
        
        Random random = new Random();
        
        try {
            // Supprimer les hébergements existants
            String deleteSQL = "DELETE FROM hebergement";
            try (Statement st = connection.createStatement()) {
                st.executeUpdate(deleteSQL);
            }
            
            // Limiter le nombre de villes pour éviter de générer trop d'hébergements
            int maxVilles = Math.min(villes.length, 10);
            String[] villesSelectionnees = Arrays.copyOf(villes, maxVilles);
            
            System.out.println("Génération d'hébergements pour " + maxVilles + " villes...");
            
            // Ajouter de nouveaux hébergements
            for (String ville : villesSelectionnees) {
                for (String type : types) {
                    // Ajouter 1-2 hébergements de chaque type dans chaque ville
                    int nbHebergements = 1 + random.nextInt(2); // 1 à 2 hébergements
                    
                    System.out.println("Ajout de " + nbHebergements + " hébergements de type " + type + " à " + ville);
                    
                    for (int i = 0; i < nbHebergements; i++) {
                        try {
                            // Choisir un nom aléatoire selon le type
                            List<String> prefixes = nomsPrefixes.get(type);
                            String prefixe = prefixes.get(random.nextInt(prefixes.size()));
                            String nom = prefixe + " " + ville;
                            
                            // Choisir une adresse aléatoire
                            String numero = String.valueOf(1 + random.nextInt(100));
                            String rue = rues[random.nextInt(rues.length)];
                            String adresse = numero + " " + rue;
                            
                            // Nombre d'étoiles (1-5)
                            int etoiles = 1 + random.nextInt(5);
                            
                            // Prix par nuit selon le type et les étoiles
                            double prixBase = switch (type) {
                                case "Hôtel" -> 80.0;
                                case "Appartement" -> 60.0;
                                case "Maison" -> 100.0;
                                case "Villa" -> 150.0;
                                case "Résidence" -> 70.0;
                                case "Gîte" -> 50.0;
                                default -> 70.0;
                            };
                            
                            // Ajuster le prix selon les étoiles et ajouter une variation aléatoire
                            double prix = prixBase * etoiles * (0.8 + random.nextDouble() * 0.4);
                            prix = Math.round(prix * 100) / 100.0; // Arrondir à 2 décimales
                            
                            // Capacité selon le type
                            int capaciteBase = switch (type) {
                                case "Hôtel" -> 2;
                                case "Appartement" -> 2;
                                case "Maison" -> 4;
                                case "Villa" -> 6;
                                case "Résidence" -> 3;
                                case "Gîte" -> 4;
                                default -> 2;
                            };
                            
                            // Ajouter une variation à la capacité
                            int capacite = capaciteBase + random.nextInt(3);
                            
                            // Choisir une description aléatoire selon le type
                            List<String> descList = descriptions.get(type);
                            String description = descList.get(random.nextInt(descList.size()));
                            
                            // Créer et ajouter l'hébergement
                            Hebergement hebergement = new Hebergement(nom, type, adresse, ville, 
                                                                     etoiles, prix, capacite, 
                                                                     description, "Disponible");
                            boolean success = ajouter(hebergement);
                            if (success) {
                                System.out.println("Hébergement ajouté: " + nom + " - " + type + " - " + ville);
                            } else {
                                System.err.println("Échec de l'ajout de l'hébergement: " + nom);
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'ajout de l'hébergement: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            System.out.println("Hébergements de démonstration ajoutés avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des hébergements de démonstration : " + e.getMessage());
        }
    }
    

    
    /**
     * Convertit un ResultSet en objet Hebergement
     * 
     * @param rs Le ResultSet à convertir
     * @return Un objet Hebergement
     * @throws SQLException Si une erreur SQL survient
     */
    private Hebergement mapResultSetToHebergement(ResultSet rs) throws SQLException {
        return new Hebergement(
                rs.getInt("idHebergement"),
                rs.getString("nom"),
                rs.getString("type"),
                rs.getString("adresse"),
                rs.getString("ville"),
                rs.getInt("etoiles"),
                rs.getDouble("prixNuit"),
                rs.getInt("capacite"),
                rs.getString("description"),
                rs.getString("disponibilite")
        );
    }
}