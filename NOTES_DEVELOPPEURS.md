# 📌 Notes de développement - Base de données

## 🔄 Modifications du 23/04/2025 par [Hamd Benelhattab]

- ✅ Table `reservation` : ajout de la colonne `statut_reservation`
- ✅ Nouvelle table `panier` avec les champs :
  - id_panier
  - id_reservation
  - id_vol
  - id_hebergement
  - created_at
  - total_a_payer
  - statut_reservation (enum: pending / validé)

**⚠️ Merci d'appliquer ces changements dans votre base locale via PhpMyAdmin**
