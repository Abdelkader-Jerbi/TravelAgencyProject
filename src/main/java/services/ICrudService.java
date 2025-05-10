package services;

import java.util.List;

/**
 * Interface générique pour les opérations CRUD
 * 
 * @param <T> Le type d'entité
 */
public interface ICrudService<T> {
    /**
     * Ajoute une entité
     * 
     * @param entity L'entité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouter(T entity);
    
    /**
     * Modifie une entité
     * 
     * @param entity L'entité à modifier
     * @return true si la modification a réussi, false sinon
     */
    boolean modifier(T entity);
    
    /**
     * Supprime une entité par son ID
     * 
     * @param id L'ID de l'entité à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimer(int id);
    
    /**
     * Récupère une entité par son ID
     * 
     * @param id L'ID de l'entité à récupérer
     * @return L'entité correspondant à l'ID, ou null si aucune entité n'est trouvée
     */
    T getById(int id);
    
    /**
     * Récupère toutes les entités
     * 
     * @return Une liste de toutes les entités
     */
    List<T> getAll();
}